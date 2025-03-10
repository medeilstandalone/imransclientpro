package com.vanuston.medeil.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRPrintXmlLoader;
import net.sf.jasperreports.view.JRHyperlinkListener;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;


public class JRViewer extends javax.swing.JPanel implements JRHyperlinkListener {
    Logger log = Logger.getLogger(JRViewer.class,"Finanical");

    protected static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
    public static final String VIEWER_RENDER_BUFFER_MAX_SIZE = JRProperties.PROPERTY_PREFIX + "viewer.render.buffer.max.size";
    protected static final int TYPE_FILE_NAME = 1;
    protected static final int TYPE_INPUT_STREAM = 2;
    protected static final int TYPE_OBJECT = 3;
    public static final int REPORT_RESOLUTION = 72;
    protected float MIN_ZOOM = 0.5f;
    protected float MAX_ZOOM = 10f;
    protected int zooms[] = {50, 75, 100, 125, 150, 175, 200, 250, 400, 800};
    protected int defaultZoomIndex = 2;
    protected int type = TYPE_FILE_NAME;
    protected boolean isXML = false;
    protected String reportFileName = null;
    protected SimpleFileResolver fileResolver = null;
    JasperPrint jasperPrint = null;
    protected int pageIndex = 0;
    protected boolean pageError;
    protected float zoom = 0f;
    protected JRGraphics2DExporter exporter = null;
    protected int screenResolution = REPORT_RESOLUTION;
    protected float realZoom = 0f;
    protected DecimalFormat zoomDecimalFormat = new DecimalFormat("#.##");
    protected ResourceBundle resourceBundle = null;
    protected int downX = 0;
    protected int downY = 0;
    protected java.util.List hyperlinkListeners = new ArrayList();
    protected Map linksMap = new HashMap();
    protected MouseListener mouseListener =
            new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    hyperlinkClicked(evt);
                }
            };
    protected KeyListener keyNavigationListener =
            new KeyListener() {

                @Override
                public void keyTyped(KeyEvent evt) {
                }

                @Override
                public void keyPressed(KeyEvent evt) {
                    keyNavigate(evt);
                }

                @Override
                public void keyReleased(KeyEvent evt) {
                }
            };
    protected List saveContributors = new ArrayList();
    protected File lastFolder = null;
    protected JRSaveContributor lastSaveContributor = null;

    public JRViewer(String fileName, boolean isXML) throws JRException {
        this(fileName, isXML, null);
    }

    public JRViewer(InputStream is, boolean isXML) throws JRException {
        this(is, isXML, null);
    }

    public JRViewer(JasperPrint jrPrint) {
        this(jrPrint, null);
    }

    public JRViewer(String fileName, boolean isXML, Locale locale) throws
            JRException {
        this(fileName, isXML, locale, null);
    }

    public JRViewer(InputStream is, boolean isXML, Locale locale) throws
            JRException {
        this(is, isXML, locale, null);
    }

    public JRViewer(JasperPrint jrPrint, Locale locale) {
        this(jrPrint, locale, null);
    }

    public JRViewer(String fileName, boolean isXML, Locale locale,
            ResourceBundle resBundle) throws JRException {
        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(fileName, isXML);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    public JRViewer(InputStream is, boolean isXML, Locale locale,
            ResourceBundle resBundle) throws JRException {
        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(is, isXML);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    public JRViewer(JasperPrint jrPrint, Locale locale, ResourceBundle resBundle) {
        initResources(locale, resBundle);

        setScreenDetails();

        setZooms();

        initComponents();

        loadReport(jrPrint);

        cmbZoom.setSelectedIndex(defaultZoomIndex);

        initSaveContributors();

        addHyperlinkListener(this);
    }

    protected void setScreenDetails() {
        screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
    }

    public void clear() {
        emptyContainer(this);
        jasperPrint = null;
    }

    protected void setZooms() {
    }

    public void addSaveContributor(JRSaveContributor contributor) {
        saveContributors.add(contributor);
    }

    public void removeSaveContributor(JRSaveContributor contributor) {
        saveContributors.remove(contributor);
    }

    public JRSaveContributor[] getSaveContributors() {
        return (JRSaveContributor[]) saveContributors.toArray(new JRSaveContributor[saveContributors.size()]);
    }

    public void setSaveContributors(JRSaveContributor[] saveContributors) {
        this.saveContributors = new ArrayList();
        if (saveContributors != null) {
            this.saveContributors.addAll(Arrays.asList(saveContributors));
        }
    }

    public void addHyperlinkListener(JRHyperlinkListener listener) {
        hyperlinkListeners.add(listener);
    }

    public void removeHyperlinkListener(JRHyperlinkListener listener) {
        hyperlinkListeners.remove(listener);
    }

    public JRHyperlinkListener[] getHyperlinkListeners() {
        return (JRHyperlinkListener[]) hyperlinkListeners.toArray(new JRHyperlinkListener[hyperlinkListeners.size()]);
    }

    protected void initResources(Locale locale, ResourceBundle resBundle) {
        if (locale != null) {
            setLocale(locale);
        } else {
            setLocale(Locale.getDefault());
        }
        if (resBundle == null) {
            this.resourceBundle = ResourceBundle.getBundle(
                    "net/sf/jasperreports/view/viewer", getLocale());
        } else {
            this.resourceBundle = resBundle;
        }
    }

    protected String getBundleString(String key) {
        return resourceBundle.getString(key);
    }

    protected void initSaveContributors() {
        final String[] DEFAULT_CONTRIBUTORS = {
            "net.sf.jasperreports.view.save.JRPrintSaveContributor",
            "net.sf.jasperreports.view.save.JRPdfSaveContributor",
            "net.sf.jasperreports.view.save.JRRtfSaveContributor",
            "net.sf.jasperreports.view.save.JROdtSaveContributor",
            "net.sf.jasperreports.view.save.JRHtmlSaveContributor",
            "net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor",
            "net.sf.jasperreports.view.save.JRMultipleSheetsXlsSaveContributor",
            "net.sf.jasperreports.view.save.JRCsvSaveContributor",
            "net.sf.jasperreports.view.save.JRXmlSaveContributor",
            "net.sf.jasperreports.view.save.JREmbeddedImagesXmlSaveContributor"
        };
        for (int i = 0; i < DEFAULT_CONTRIBUTORS.length; i++) {
            try {
                Class saveContribClass = JRClassLoader.loadClassForName(
                        DEFAULT_CONTRIBUTORS[i]);
                Constructor constructor = saveContribClass.getConstructor(
                        new Class[]{Locale.class, ResourceBundle.class});
                JRSaveContributor saveContrib = (JRSaveContributor) constructor.newInstance(new Object[]{getLocale(), resourceBundle});
                saveContributors.add(saveContrib);
            } catch (Exception e) {
            }
        }
    }

    public void gotoHyperlink(JRPrintHyperlink hyperlink) {
        switch (hyperlink.getHyperlinkType()) {
            case JRHyperlink.HYPERLINK_TYPE_REFERENCE: {
                if (isOnlyHyperlinkListener()) {
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_LOCAL_ANCHOR: {
                if (hyperlink.getHyperlinkAnchor() != null) {
                    Map anchorIndexes = jasperPrint.getAnchorIndexes();
                    JRPrintAnchorIndex anchorIndex =
                            (JRPrintAnchorIndex) anchorIndexes.get(hyperlink.getHyperlinkAnchor());
                    if (anchorIndex.getPageIndex() != pageIndex) {
                        setPageIndex(anchorIndex.getPageIndex());
                        refreshPage();
                    }
                    Container container = pnlInScroll.getParent();
                    if (container instanceof JViewport) {
                        JViewport viewport = (JViewport) container;
                        int newX = (int) (anchorIndex.getElementAbsoluteX()
                                * realZoom);
                        int newY = (int) (anchorIndex.getElementAbsoluteY()
                                * realZoom);
                        int maxX = pnlInScroll.getWidth() - viewport.getWidth();
                        int maxY = pnlInScroll.getHeight()
                                - viewport.getHeight();

                        if (newX < 0) {
                            newX = 0;
                        }
                        if (newX > maxX) {
                            newX = maxX;
                        }
                        if (newY < 0) {
                            newY = 0;
                        }
                        if (newY > maxY) {
                            newY = maxY;
                        }
                        viewport.setViewPosition(new Point(newX, newY));
                    }
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_LOCAL_PAGE: {
                int page = pageIndex + 1;
                if (hyperlink.getHyperlinkPage() != null) {
                    page = hyperlink.getHyperlinkPage().intValue();
                }
                if (page >= 1 && page <= jasperPrint.getPages().size()
                        && page != pageIndex + 1) {
                    setPageIndex(page - 1);
                    refreshPage();
                    Container container = pnlInScroll.getParent();
                    if (container instanceof JViewport) {
                        JViewport viewport = (JViewport) container;
                        viewport.setViewPosition(new Point(0, 0));
                    }
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_REMOTE_ANCHOR: {
                if (isOnlyHyperlinkListener()) {
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_REMOTE_PAGE: {
                if (isOnlyHyperlinkListener()) {
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_CUSTOM: {
                if (isOnlyHyperlinkListener()) {
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_NONE:
            default: {
                break;
            }
        }
    }

    protected boolean isOnlyHyperlinkListener() {
        int listenerCount;
        if (hyperlinkListeners == null) {
            listenerCount = 0;
        } else {
            listenerCount = hyperlinkListeners.size();
            if (hyperlinkListeners.contains(this)) {
                --listenerCount;
            }
        }
        return listenerCount == 0;
    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    protected void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tlbToolBar = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        pnlSep01 = new javax.swing.JPanel();
        btnFirst = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        txtGoTo = new javax.swing.JTextField();
        pnlSep02 = new javax.swing.JPanel();
        btnActualSize = new javax.swing.JToggleButton();
        btnFitPage = new javax.swing.JToggleButton();
        btnFitWidth = new javax.swing.JToggleButton();
        pnlSep03 = new javax.swing.JPanel();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        cmbZoom = new javax.swing.JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < zooms.length; i++) {
            model.addElement("" + zooms[i] + "%");
        }
        cmbZoom.setModel(model);

        pnlMain = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);

        pnlInScroll = new javax.swing.JPanel();
        pnlPage = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        pnlLinks = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblPage = new PageRenderer(this);
        pnlStatus = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        setMinimumSize(new java.awt.Dimension(450, 150));
        setPreferredSize(new java.awt.Dimension(450, 150));
        tlbToolBar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0,
                2));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/save.GIF")));
        btnSave.setToolTipText(getBundleString("save"));
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.setMaximumSize(new java.awt.Dimension(23, 23));
        btnSave.setMinimumSize(new java.awt.Dimension(23, 23));
        btnSave.setPreferredSize(new java.awt.Dimension(23, 23));
        btnSave.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        btnSave.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnSave);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/print.GIF")));
        btnPrint.setToolTipText(getBundleString("print"));
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setMaximumSize(new java.awt.Dimension(23, 23));
        btnPrint.setMinimumSize(new java.awt.Dimension(23, 23));
        btnPrint.setPreferredSize(new java.awt.Dimension(23, 23));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        btnPrint.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnPrint);

        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/reload.GIF")));
        btnReload.setToolTipText(getBundleString("reload"));
        btnReload.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnReload.setMaximumSize(new java.awt.Dimension(23, 23));
        btnReload.setMinimumSize(new java.awt.Dimension(23, 23));
        btnReload.setPreferredSize(new java.awt.Dimension(23, 23));
        btnReload.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });
        btnReload.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnReload);

        pnlSep01.setMaximumSize(new java.awt.Dimension(10, 10));
        tlbToolBar.add(pnlSep01);

        btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/first.GIF")));
        btnFirst.setToolTipText(getBundleString("first.page"));
        btnFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnFirst.setMaximumSize(new java.awt.Dimension(23, 23));
        btnFirst.setMinimumSize(new java.awt.Dimension(23, 23));
        btnFirst.setPreferredSize(new java.awt.Dimension(23, 23));
        btnFirst.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        btnFirst.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnFirst);

        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/previous.GIF")));
        btnPrevious.setToolTipText(getBundleString("previous.page"));
        btnPrevious.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrevious.setMaximumSize(new java.awt.Dimension(23, 23));
        btnPrevious.setMinimumSize(new java.awt.Dimension(23, 23));
        btnPrevious.setPreferredSize(new java.awt.Dimension(23, 23));
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        btnPrevious.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnPrevious);

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/next.GIF")));
        btnNext.setToolTipText(getBundleString("next.page"));
        btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNext.setMaximumSize(new java.awt.Dimension(23, 23));
        btnNext.setMinimumSize(new java.awt.Dimension(23, 23));
        btnNext.setPreferredSize(new java.awt.Dimension(23, 23));
        btnNext.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        btnNext.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnNext);

        btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/last.GIF")));
        btnLast.setToolTipText(getBundleString("last.page"));
        btnLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnLast.setMaximumSize(new java.awt.Dimension(23, 23));
        btnLast.setMinimumSize(new java.awt.Dimension(23, 23));
        btnLast.setPreferredSize(new java.awt.Dimension(23, 23));
        btnLast.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        btnLast.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnLast);

        txtGoTo.setToolTipText(getBundleString("go.to.page"));
        txtGoTo.setMaximumSize(new java.awt.Dimension(40, 23));
        txtGoTo.setMinimumSize(new java.awt.Dimension(40, 23));
        txtGoTo.setPreferredSize(new java.awt.Dimension(40, 23));
        txtGoTo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGoToActionPerformed(evt);
            }
        });
        txtGoTo.addKeyListener(keyNavigationListener);
        tlbToolBar.add(txtGoTo);

        pnlSep02.setMaximumSize(new java.awt.Dimension(10, 10));
        tlbToolBar.add(pnlSep02);

        btnActualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/actualsize.GIF")));
        btnActualSize.setToolTipText(getBundleString("actual.size"));
        btnActualSize.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnActualSize.setMaximumSize(new java.awt.Dimension(23, 23));
        btnActualSize.setMinimumSize(new java.awt.Dimension(23, 23));
        btnActualSize.setPreferredSize(new java.awt.Dimension(23, 23));
        btnActualSize.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualSizeActionPerformed(evt);
            }
        });
        btnActualSize.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnActualSize);

        btnFitPage.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/fitpage.GIF")));
        btnFitPage.setToolTipText(getBundleString("fit.page"));
        btnFitPage.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnFitPage.setMaximumSize(new java.awt.Dimension(23, 23));
        btnFitPage.setMinimumSize(new java.awt.Dimension(23, 23));
        btnFitPage.setPreferredSize(new java.awt.Dimension(23, 23));
        btnFitPage.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitPageActionPerformed(evt);
            }
        });
        btnFitPage.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnFitPage);

        btnFitWidth.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/fitwidth.GIF")));
        btnFitWidth.setToolTipText(getBundleString("fit.width"));
        btnFitWidth.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnFitWidth.setMaximumSize(new java.awt.Dimension(23, 23));
        btnFitWidth.setMinimumSize(new java.awt.Dimension(23, 23));
        btnFitWidth.setPreferredSize(new java.awt.Dimension(23, 23));
        btnFitWidth.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFitWidthActionPerformed(evt);
            }
        });
        btnFitWidth.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnFitWidth);

        pnlSep03.setMaximumSize(new java.awt.Dimension(10, 10));
        tlbToolBar.add(pnlSep03);

        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/zoomin.GIF")));
        btnZoomIn.setToolTipText(getBundleString("zoom.in"));
        btnZoomIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnZoomIn.setMaximumSize(new java.awt.Dimension(23, 23));
        btnZoomIn.setMinimumSize(new java.awt.Dimension(23, 23));
        btnZoomIn.setPreferredSize(new java.awt.Dimension(23, 23));
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        btnZoomIn.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnZoomIn);

        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/net/sf/jasperreports/view/images/zoomout.GIF")));
        btnZoomOut.setToolTipText(getBundleString("zoom.out"));
        btnZoomOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnZoomOut.setMaximumSize(new java.awt.Dimension(23, 23));
        btnZoomOut.setMinimumSize(new java.awt.Dimension(23, 23));
        btnZoomOut.setPreferredSize(new java.awt.Dimension(23, 23));
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        btnZoomOut.addKeyListener(keyNavigationListener);
        tlbToolBar.add(btnZoomOut);

        cmbZoom.setEditable(true);
        cmbZoom.setToolTipText(getBundleString("zoom.ratio"));
        cmbZoom.setMaximumSize(new java.awt.Dimension(80, 23));
        cmbZoom.setMinimumSize(new java.awt.Dimension(80, 23));
        cmbZoom.setPreferredSize(new java.awt.Dimension(80, 23));
        cmbZoom.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbZoomActionPerformed(evt);
            }
        });
        cmbZoom.addItemListener(new java.awt.event.ItemListener() {

            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbZoomItemStateChanged(evt);
            }
        });
        cmbZoom.addKeyListener(keyNavigationListener);
        tlbToolBar.add(cmbZoom);

        add(tlbToolBar, java.awt.BorderLayout.NORTH);

        pnlMain.setLayout(new java.awt.BorderLayout());
        pnlMain.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlMainComponentResized(evt);
            }
        });

        scrollPane.setHorizontalScrollBarPolicy(
                javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
                javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pnlInScroll.setLayout(new java.awt.GridBagLayout());

        pnlPage.setLayout(new java.awt.BorderLayout());
        pnlPage.setMinimumSize(new java.awt.Dimension(100, 100));
        pnlPage.setPreferredSize(new java.awt.Dimension(100, 100));

        jPanel4.setLayout(new java.awt.GridBagLayout());
        jPanel4.setMinimumSize(new java.awt.Dimension(100, 120));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 120));

        pnlLinks.setLayout(null);
        pnlLinks.setMinimumSize(new java.awt.Dimension(5, 5));
        pnlLinks.setPreferredSize(new java.awt.Dimension(5, 5));
        pnlLinks.setOpaque(false);
        pnlLinks.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlLinksMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlLinksMouseReleased(evt);
            }
        });
        pnlLinks.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlLinksMouseDragged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(pnlLinks, gridBagConstraints);

        jPanel5.setBackground(java.awt.Color.gray);
        jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel6.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel7.setBackground(java.awt.Color.gray);
        jPanel7.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel7.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel7, gridBagConstraints);

        jPanel8.setBackground(java.awt.Color.gray);
        jPanel8.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel8.setPreferredSize(new java.awt.Dimension(5, 5));
        jLabel1.setText("jLabel1");
        jPanel8.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel4.add(jPanel8, gridBagConstraints);

        jPanel9.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel9.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jPanel9, gridBagConstraints);

        lblPage.setBackground(java.awt.Color.white);
        lblPage.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0,
                0, 0)));
        lblPage.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(lblPage, gridBagConstraints);

        pnlPage.add(jPanel4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInScroll.add(pnlPage, gridBagConstraints);

        scrollPane.setViewportView(pnlInScroll);
        pnlMain.add(scrollPane, java.awt.BorderLayout.CENTER);
        add(pnlMain, java.awt.BorderLayout.CENTER);

        pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,
                0, 0));

        lblStatus.setFont(new java.awt.Font("Dialog", 1, 10));
        lblStatus.setText("Page i of n");
        pnlStatus.add(lblStatus);
        add(pnlStatus, java.awt.BorderLayout.SOUTH);
        addKeyListener(keyNavigationListener);
    }
    // </editor-fold>

    void txtGoToActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int pageNumber = Integer.parseInt(txtGoTo.getText());
            if (pageNumber != pageIndex + 1 && pageNumber > 0 && pageNumber <= jasperPrint.getPages().
                    size()) {
                setPageIndex(pageNumber - 1);
                refreshPage();
            }
        } catch (NumberFormatException e) {
        }
    }

    void cmbZoomItemStateChanged(java.awt.event.ItemEvent evt) {
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);
    }

    void pnlMainComponentResized(java.awt.event.ComponentEvent evt) {
        if (btnFitPage.isSelected()) {
            fitPage();
            btnFitPage.setSelected(true);
        } else if (btnFitWidth.isSelected()) {
            setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth()
                    - 20f) / jasperPrint.getPageWidth());
            btnFitWidth.setSelected(true);
        }
    }

    void btnActualSizeActionPerformed(java.awt.event.ActionEvent evt) {
        if (btnActualSize.isSelected()) {
            btnFitPage.setSelected(false);
            btnFitWidth.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            setZoomRatio(1);
            btnActualSize.setSelected(true);
        }
    }

    void btnFitWidthActionPerformed(java.awt.event.ActionEvent evt) {
        if (btnFitWidth.isSelected()) {
            btnActualSize.setSelected(false);
            btnFitPage.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth()
                    - 20f) / jasperPrint.getPageWidth());
            btnFitWidth.setSelected(true);
        }
    }

    void btnFitPageActionPerformed(java.awt.event.ActionEvent evt) {
        if (btnFitPage.isSelected()) {
            btnActualSize.setSelected(false);
            btnFitWidth.setSelected(false);
            cmbZoom.setSelectedIndex(-1);
            fitPage();
            btnFitPage.setSelected(true);
        }
    }

    void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setLocale(this.getLocale());
        fileChooser.updateUI();
        for (int i = 0; i < saveContributors.size(); i++) {
            fileChooser.addChoosableFileFilter((JRSaveContributor) saveContributors.get(i));
        }
        if (saveContributors.contains(lastSaveContributor)) {
            fileChooser.setFileFilter(lastSaveContributor);
        } else if (saveContributors.size() > 0) {
            fileChooser.setFileFilter(
                    (JRSaveContributor) saveContributors.get(0));
        }
        if (lastFolder != null) {
            fileChooser.setCurrentDirectory(lastFolder);
        }
        int retValue = fileChooser.showSaveDialog(this);
        if (retValue == JFileChooser.APPROVE_OPTION) {
            FileFilter fileFilter = fileChooser.getFileFilter();
            File file = fileChooser.getSelectedFile();
            lastFolder = file.getParentFile();
            JRSaveContributor contributor = null;
            if (fileFilter instanceof JRSaveContributor) {
                contributor = (JRSaveContributor) fileFilter;
            } else {
                int i = 0;
                while (contributor == null && i < saveContributors.size()) {
                    contributor = (JRSaveContributor) saveContributors.get(i++);
                    if (!contributor.accept(file)) {
                        contributor = null;
                    }
                }
                if (contributor == null) {
                    contributor = new JRPrintSaveContributor(getLocale(),
                            this.resourceBundle);
                }
            }
            lastSaveContributor = contributor;
            try {
                contributor.save(jasperPrint, file);
            } catch (JRException e) {

                JOptionPane.showMessageDialog(this, getBundleString(
                        "error.saving"));
            }
        }
    }

    void pnlLinksMouseDragged(java.awt.event.MouseEvent evt) {
        Container container = pnlInScroll.getParent();
        if (container instanceof JViewport) {
            JViewport viewport = (JViewport) container;
            Point point = viewport.getViewPosition();
            int newX = point.x - (evt.getX() - downX);
            int newY = point.y - (evt.getY() - downY);
            int maxX = pnlInScroll.getWidth() - viewport.getWidth();
            int maxY = pnlInScroll.getHeight() - viewport.getHeight();
            if (newX < 0) {
                newX = 0;
            }
            if (newX > maxX) {
                newX = maxX;
            }
            if (newY < 0) {
                newY = 0;
            }
            if (newY > maxY) {
                newY = maxY;
            }
            viewport.setViewPosition(new Point(newX, newY));
        }
    }

    void pnlLinksMouseReleased(java.awt.event.MouseEvent evt) {
        pnlLinks.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    void pnlLinksMousePressed(java.awt.event.MouseEvent evt) {
        pnlLinks.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        downX = evt.getX();
        downY = evt.getY();
    }

    void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    btnPrint.setEnabled(false);
                    JRViewer.this.setCursor(Cursor.getPredefinedCursor(
                            Cursor.WAIT_CURSOR));
                    JasperPrintManager.printReport(jasperPrint, true);
                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(JRViewer.this,
                            getBundleString("error.printing"));
                } finally {
                    JRViewer.this.setCursor(Cursor.getPredefinedCursor(
                            Cursor.DEFAULT_CURSOR));
                    btnPrint.setEnabled(true);
                }
            }
        });
    }

    void btnLastActionPerformed(java.awt.event.ActionEvent evt) {
        setPageIndex(jasperPrint.getPages().size() - 1);
        refreshPage();
    }

    void btnNextActionPerformed(java.awt.event.ActionEvent evt) {
        setPageIndex(pageIndex + 1);
        refreshPage();
    }

    void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {
        setPageIndex(pageIndex - 1);
        refreshPage();
    }

    void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {
        setPageIndex(0);
        refreshPage();
    }

    void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {
        if (type == TYPE_FILE_NAME) {
            try {
                loadReport(reportFileName, isXML);
            } catch (JRException e) {

                jasperPrint = null;
                setPageIndex(0);
                refreshPage();
                JOptionPane.showMessageDialog(this, getBundleString(
                        "error.loading"));
            }
            forceRefresh();
        }
    }

    protected void forceRefresh() {
        zoom = 0;
        realZoom = 0f;
        setZoomRatio(1);
    }

    void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);
        int newZoomInt = (int) (100 * getZoomRatio());
        int index = Arrays.binarySearch(zooms, newZoomInt);
        if (index < 0) {
            setZoomRatio(zooms[-index - 1] / 100f);
        } else if (index < cmbZoom.getModel().getSize() - 1) {
            setZoomRatio(zooms[index + 1] / 100f);
        }
    }

    void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {
        btnActualSize.setSelected(false);
        btnFitPage.setSelected(false);
        btnFitWidth.setSelected(false);
        int newZoomInt = (int) (100 * getZoomRatio());
        int index = Arrays.binarySearch(zooms, newZoomInt);
        if (index > 0) {
            setZoomRatio(zooms[index - 1] / 100f);
        } else if (index < -1) {
            setZoomRatio(zooms[-index - 2] / 100f);
        }
    }

    void cmbZoomActionPerformed(java.awt.event.ActionEvent evt) {
        float newZoom = getZoomRatio();
        if (newZoom < MIN_ZOOM) {
            newZoom = MIN_ZOOM;
        }
        if (newZoom > MAX_ZOOM) {
            newZoom = MAX_ZOOM;
        }
        setZoomRatio(newZoom);
    }

    void hyperlinkClicked(MouseEvent evt) {
        JPanel link = (JPanel) evt.getSource();
        JRPrintHyperlink element = (JRPrintHyperlink) linksMap.get(link);
        hyperlinkClicked(element);
    }

    protected void hyperlinkClicked(JRPrintHyperlink hyperlink) {
        try {
            JRHyperlinkListener listener = null;
            for (int i = 0; i < hyperlinkListeners.size(); i++) {
                listener = (JRHyperlinkListener) hyperlinkListeners.get(i);
                listener.gotoHyperlink(hyperlink);
            }
        } catch (JRException e) {
           
            JOptionPane.showMessageDialog(this, getBundleString(
                    "error.hyperlink"));
        }
    }

    public int getPageIndex() {
        return pageIndex;
    }

    protected void setPageIndex(int index) {
        if (jasperPrint != null
                && jasperPrint.getPages() != null
                && jasperPrint.getPages().size() > 0) {
            if (index >= 0 && index < jasperPrint.getPages().size()) {
                pageIndex = index;
                pageError = false;
                btnFirst.setEnabled((pageIndex > 0));
                btnPrevious.setEnabled((pageIndex > 0));
                btnNext.setEnabled((pageIndex < jasperPrint.getPages().size()
                        - 1));
                btnLast.setEnabled((pageIndex < jasperPrint.getPages().size()
                        - 1));
                txtGoTo.setEnabled(btnFirst.isEnabled() || btnLast.isEnabled());
                txtGoTo.setText("" + (pageIndex + 1));
                lblStatus.setText(
                        MessageFormat.format(
                        getBundleString("page"),
                        new Object[]{new Integer(pageIndex + 1), new Integer(jasperPrint.getPages().
                            size())}));
            }
        } else {
            btnFirst.setEnabled(false);
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnLast.setEnabled(false);
            txtGoTo.setEnabled(false);
            txtGoTo.setText("");
            lblStatus.setText("");
        }
    }

    protected void loadReport(String fileName, boolean isXmlReport) throws
            JRException {
        if (isXmlReport) {
            jasperPrint = JRPrintXmlLoader.load(fileName);
        } else {
            jasperPrint = (JasperPrint) JRLoader.loadObject(fileName);
        }
        type = TYPE_FILE_NAME;
        this.isXML = isXmlReport;
        reportFileName = fileName;
        fileResolver = new SimpleFileResolver(Arrays.asList(new File[]{new File(
                    fileName).getParentFile(), new File(".")}));
        fileResolver.setResolveAbsolutePath(true);
        btnReload.setEnabled(true);
        setPageIndex(0);
    }

    protected void loadReport(InputStream is, boolean isXmlReport) throws
            JRException {
        if (isXmlReport) {
            jasperPrint = JRPrintXmlLoader.load(is);
        } else {
            jasperPrint = (JasperPrint) JRLoader.loadObject(is);
        }
        type = TYPE_INPUT_STREAM;
        this.isXML = isXmlReport;
        btnReload.setEnabled(false);
        setPageIndex(0);
    }

    protected void loadReport(JasperPrint jrPrint) {
        jasperPrint = jrPrint;
        type = TYPE_OBJECT;
        isXML = false;
        btnReload.setEnabled(false);
        setPageIndex(0);
    }

    protected void refreshPage() {
        if (jasperPrint == null
                || jasperPrint.getPages() == null
                || jasperPrint.getPages().size() == 0) {
            pnlPage.setVisible(false);
            btnSave.setEnabled(false);
            btnPrint.setEnabled(false);
            btnActualSize.setEnabled(false);
            btnFitPage.setEnabled(false);
            btnFitWidth.setEnabled(false);
            btnZoomIn.setEnabled(false);
            btnZoomOut.setEnabled(false);
            cmbZoom.setEnabled(false);
            if (jasperPrint != null) {
                JOptionPane.showMessageDialog(this, getBundleString("no.pages"));
            }
            return;
        }
        pnlPage.setVisible(true);
        btnSave.setEnabled(true);
        btnPrint.setEnabled(true);
        btnActualSize.setEnabled(true);
        btnFitPage.setEnabled(true);
        btnFitWidth.setEnabled(true);
        btnZoomIn.setEnabled(zoom < MAX_ZOOM);
        btnZoomOut.setEnabled(zoom > MIN_ZOOM);
        cmbZoom.setEnabled(true);
        Dimension dim = new Dimension(
                (int) (jasperPrint.getPageWidth() * realZoom) + 8,
                (int) (jasperPrint.getPageHeight() * realZoom) + 8);
        pnlPage.setMaximumSize(dim);
        pnlPage.setMinimumSize(dim);
        pnlPage.setPreferredSize(dim);
        long maxImageSize = JRProperties.getLongProperty(
                VIEWER_RENDER_BUFFER_MAX_SIZE);
        boolean renderImage;
        if (maxImageSize <= 0) {
            renderImage = false;
        } else {
            long imageSize = JRPrinterAWT.getImageSize(jasperPrint, realZoom);
            renderImage = imageSize <= maxImageSize;
        }
        lblPage.setRenderImage(renderImage);
        if (renderImage) {
            setPageImage();
        }
        pnlLinks.removeAll();
        linksMap = new HashMap();
        createHyperlinks();
        if (!renderImage) {
            lblPage.setIcon(null);
            pnlMain.validate();
            pnlMain.repaint();
        }
    }

    protected void setPageImage() {
        Image image;
        if (pageError) {
            image = getPageErrorImage();
        } else {
            try {
                image = JasperPrintManager.printPageToImage(jasperPrint,
                        pageIndex, realZoom);
            } catch (Exception e) {
                
                pageError = true;
                image = getPageErrorImage();
                JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString(
                        "error.displaying"));
            }
        }
        ImageIcon imageIcon = new ImageIcon(image);
        lblPage.setIcon(imageIcon);
    }

    protected Image getPageErrorImage() {
        Image image = new BufferedImage(
                (int) (jasperPrint.getPageWidth() * realZoom) + 1,
                (int) (jasperPrint.getPageHeight() * realZoom) + 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D grx = (Graphics2D) image.getGraphics();
        AffineTransform transform = new AffineTransform();
        transform.scale(realZoom, realZoom);
        grx.transform(transform);
        drawPageError((Graphics2D) grx);
        return image;
    }

    protected void createHyperlinks() {
        java.util.List pages = jasperPrint.getPages();
        JRPrintPage page = (JRPrintPage) pages.get(pageIndex);
        createHyperlinks(page.getElements(), 0, 0);
    }

    protected void createHyperlinks(List elements, int offsetX, int offsetY) {
        if (elements != null && elements.size() > 0) {
            for (Iterator it = elements.iterator(); it.hasNext();) {
                JRPrintElement element = (JRPrintElement) it.next();
                JRImageMapRenderer imageMap = null;
                if (element instanceof JRPrintImage) {
                    JRRenderable renderer =
                            ((JRPrintImage) element).getRenderer();
                    if (renderer instanceof JRImageMapRenderer) {
                        imageMap = (JRImageMapRenderer) renderer;
                        if (!imageMap.hasImageAreaHyperlinks()) {
                            imageMap = null;
                        }
                    }
                }
                boolean hasImageMap = imageMap != null;
                JRPrintHyperlink hyperlink = null;
                if (element instanceof JRPrintHyperlink) {
                    hyperlink = (JRPrintHyperlink) element;
                }
                boolean hasHyperlink = !hasImageMap && hyperlink != null
                        && hyperlink.getHyperlinkType()
                        != JRHyperlink.HYPERLINK_TYPE_NONE;
                boolean hasTooltip = hyperlink != null && hyperlink.getHyperlinkTooltip() != null;
                if (hasHyperlink || hasImageMap || hasTooltip) {
                    JPanel link;
                    if (hasImageMap) {
                        Rectangle renderingArea = new Rectangle(0, 0, element.getWidth(), element.getHeight());
                        link = new ImageMapPanel(renderingArea, imageMap);
                    } else {
                        link = new JPanel();
                        if (hasHyperlink) {
                            link.addMouseListener(mouseListener);
                        }
                    }
                    if (hasHyperlink) {
                        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                    link.setLocation(
                            (int) ((element.getX() + offsetX) * realZoom),
                            (int) ((element.getY() + offsetY) * realZoom));
                    link.setSize(
                            (int) (element.getWidth() * realZoom),
                            (int) (element.getHeight() * realZoom));
                    link.setOpaque(false);
                    String toolTip = getHyperlinkTooltip(hyperlink);
                    if (toolTip == null && hasImageMap) {
                        toolTip = "";
                    }
                    link.setToolTipText(toolTip);
                    pnlLinks.add(link);
                    linksMap.put(link, element);
                }
                if (element instanceof JRPrintFrame) {
                    JRPrintFrame frame = (JRPrintFrame) element;
                    int frameOffsetX = offsetX + frame.getX() + frame.getLineBox().
                            getLeftPadding().intValue();
                    int frameOffsetY = offsetY + frame.getY() + frame.getLineBox().
                            getTopPadding().intValue();
                    createHyperlinks(frame.getElements(), frameOffsetX,
                            frameOffsetY);
                }
            }
        }
    }

    protected class ImageMapPanel extends JPanel implements MouseListener,
            MouseMotionListener {

        protected static final long serialVersionUID =
                JRConstants.SERIAL_VERSION_UID;
        protected final List imageAreaHyperlinks;

        public ImageMapPanel(Rectangle renderingArea,
                JRImageMapRenderer imageMap) {
            try {
                imageAreaHyperlinks = imageMap.getImageAreaHyperlinks(
                        renderingArea);
            } catch (JRException e) {
                throw new JRRuntimeException(e);
            }
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            String tooltip = null;
            JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(event);
            if (imageMapArea != null) {
                tooltip = getHyperlinkTooltip(imageMapArea.getHyperlink());
            }
            if (tooltip == null) {
                tooltip = super.getToolTipText(event);
            }
            return tooltip;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            pnlLinksMouseDragged(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JRPrintImageAreaHyperlink imageArea = getImageMapArea(e);
            if (imageArea != null
                    && imageArea.getHyperlink().getHyperlinkType()
                    != JRHyperlink.HYPERLINK_TYPE_NONE) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));
            } else {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        }

        protected JRPrintImageAreaHyperlink getImageMapArea(MouseEvent e) {
            return getImageMapArea((int) (e.getX() / realZoom),
                    (int) (e.getY() / realZoom));
        }

        protected JRPrintImageAreaHyperlink getImageMapArea(int x, int y) {
            JRPrintImageAreaHyperlink image = null;
            if (imageAreaHyperlinks != null) {
                for (ListIterator it = imageAreaHyperlinks.listIterator(
                        imageAreaHyperlinks.size()); image == null && it.hasPrevious();) {
                    JRPrintImageAreaHyperlink area =
                            (JRPrintImageAreaHyperlink) it.previous();
                    if (area.getArea().containsPoint(x, y)) {
                        image = area;
                    }
                }
            }
            return image;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(e);
            if (imageMapArea != null) {
                hyperlinkClicked(imageMapArea.getHyperlink());
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            e.getComponent().setCursor(Cursor.getPredefinedCursor(
                    Cursor.MOVE_CURSOR));
            pnlLinksMousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
            pnlLinksMouseReleased(e);
        }
    }

    protected String getHyperlinkTooltip(JRPrintHyperlink hyperlink) {
        String toolTip;
        toolTip = hyperlink.getHyperlinkTooltip();
        if (toolTip == null) {
            toolTip = getFallbackTooltip(hyperlink);
        }
        return toolTip;
    }

    protected String getFallbackTooltip(JRPrintHyperlink hyperlink) {
        String toolTip = null;
        switch (hyperlink.getHyperlinkType()) {
            case JRHyperlink.HYPERLINK_TYPE_REFERENCE: {
                toolTip = hyperlink.getHyperlinkReference();
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_LOCAL_ANCHOR: {
                if (hyperlink.getHyperlinkAnchor() != null) {
                    toolTip = "#" + hyperlink.getHyperlinkAnchor();
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_LOCAL_PAGE: {
                if (hyperlink.getHyperlinkPage() != null) {
                    toolTip = "#page " + hyperlink.getHyperlinkPage();
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_REMOTE_ANCHOR: {
                toolTip = "";
                if (hyperlink.getHyperlinkReference() != null) {
                    toolTip = toolTip + hyperlink.getHyperlinkReference();
                }
                if (hyperlink.getHyperlinkAnchor() != null) {
                    toolTip = toolTip + "#" + hyperlink.getHyperlinkAnchor();
                }
                break;
            }
            case JRHyperlink.HYPERLINK_TYPE_REMOTE_PAGE: {
                toolTip = "";
                if (hyperlink.getHyperlinkReference() != null) {
                    toolTip = toolTip + hyperlink.getHyperlinkReference();
                }
                if (hyperlink.getHyperlinkPage() != null) {
                    toolTip = toolTip + "#page " + hyperlink.getHyperlinkPage();
                }
                break;
            }
            default: {
                break;
            }
        }
        return toolTip;
    }

    protected void emptyContainer(Container container) {
        Component[] components = container.getComponents();

        if (components != null) {
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof Container) {
                    emptyContainer((Container) components[i]);
                }
            }
        }

        components = null;
        container.removeAll();
        container = null;
    }

    protected float getZoomRatio() {
        float newZoom = zoom;
        try {
            newZoom =
                    zoomDecimalFormat.parse(
                    String.valueOf(cmbZoom.getEditor().getItem())).floatValue()
                    / 100f;
        } catch (ParseException e) {
        }
        return newZoom;
    }

    public void setZoomRatio(float newZoom) {
        if (newZoom > 0) {
            cmbZoom.getEditor().setItem(
                    zoomDecimalFormat.format(newZoom * 100) + "%");
            if (zoom != newZoom) {
                zoom = newZoom;
                realZoom = zoom * screenResolution / REPORT_RESOLUTION;
                refreshPage();
            }
        }
    }

    protected void setRealZoomRatio(float newZoom) {
        if (newZoom > 0 && realZoom != newZoom) {
            zoom = newZoom * REPORT_RESOLUTION / screenResolution;
            realZoom = newZoom;
            cmbZoom.getEditor().setItem(
                    zoomDecimalFormat.format(zoom * 100) + "%");
            refreshPage();
        }
    }

    public void setFitWidthZoomRatio() {
        setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getWidth() - 20f)
                / jasperPrint.getPageWidth());

    }

    public void setFitPageZoomRatio() {
        setRealZoomRatio(((float) pnlInScroll.getVisibleRect().getHeight() - 20f)
                / jasperPrint.getPageHeight());
    }

    protected JRGraphics2DExporter getGraphics2DExporter() throws JRException {
        return new JRGraphics2DExporter();
    }

    protected void paintPage(Graphics2D grx) {
        if (pageError) {
            paintPageError(grx);
            return;
        }
        try {
            if (exporter == null) {
                exporter = getGraphics2DExporter();
            } else {
                exporter.reset();
            }
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D,
                    grx.create());
            exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer(
                    pageIndex));
            exporter.setParameter(JRGraphics2DExporterParameter.ZOOM_RATIO,
                    new Float(realZoom));
            exporter.setParameter(JRExporterParameter.OFFSET_X, new Integer(1)); //lblPage border
            exporter.setParameter(JRExporterParameter.OFFSET_Y, new Integer(1));
            if (type == TYPE_FILE_NAME) {
                exporter.setParameter(JRExporterParameter.FILE_RESOLVER,
                        fileResolver);
            }
            exporter.exportReport();
        } catch (Exception e) {

            pageError = true;
            paintPageError(grx);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    JOptionPane.showMessageDialog(JRViewer.this,
                            getBundleString("error.displaying"));
                }
            });
        }
    }

    protected void paintPageError(Graphics2D grx) {
        AffineTransform origTransform = grx.getTransform();
        AffineTransform transform = new AffineTransform();
        transform.translate(1, 1);
        transform.scale(realZoom, realZoom);
        grx.transform(transform);
        try {
            drawPageError(grx);
        } finally {
            grx.setTransform(origTransform);
        }
    }

    protected void drawPageError(Graphics grx) {
        grx.setColor(Color.white);
        grx.fillRect(0, 0, jasperPrint.getPageWidth() + 1, jasperPrint.getPageHeight() + 1);
    }

    protected void keyNavigate(KeyEvent evt) {
        boolean refresh = true;
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_PAGE_DOWN:
                dnNavigate(evt);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_PAGE_UP:
                upNavigate(evt);
                break;
            case KeyEvent.VK_HOME:
                homeEndNavigate(0);
                break;
            case KeyEvent.VK_END:
                homeEndNavigate(jasperPrint.getPages().size() - 1);
                break;
            default:
                refresh = false;
        }
        if (refresh) {
            refreshPage();
        }
    }

    protected void dnNavigate(KeyEvent evt) {
        int bottomPosition = scrollPane.getVerticalScrollBar().getValue();
        scrollPane.dispatchEvent(evt);
        if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight()
                || scrollPane.getVerticalScrollBar().getValue() == bottomPosition)
                && pageIndex < jasperPrint.getPages().size() - 1) {
            setPageIndex(pageIndex + 1);
            if (scrollPane.isEnabled()) {
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        }
    }

    protected void upNavigate(KeyEvent evt) {
        if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight()
                || scrollPane.getVerticalScrollBar().getValue() == 0)
                && pageIndex > 0) {
            setPageIndex(pageIndex - 1);
            if (scrollPane.isEnabled()) {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        } else {
            scrollPane.dispatchEvent(evt);
        }
    }

    protected void homeEndNavigate(int pageNumber) {
        setPageIndex(pageNumber);
        if (scrollPane.isEnabled()) {
            scrollPane.getVerticalScrollBar().setValue(0);
        }
    }

    protected void fitPage() {
        float heightRatio = ((float) pnlInScroll.getVisibleRect().getHeight()
                - 20f) / jasperPrint.getPageHeight();
        float widthRatio = ((float) pnlInScroll.getVisibleRect().getWidth()
                - 20f) / jasperPrint.getPageWidth();
        setRealZoomRatio(heightRatio < widthRatio ? heightRatio : widthRatio);
    }

    class PageRenderer extends JLabel {

        protected static final long serialVersionUID =
                JRConstants.SERIAL_VERSION_UID;
        protected boolean renderImage;
        JRViewer viewer = null;

        public PageRenderer(JRViewer viewer) {
            this.viewer = viewer;
        }

        @Override
        public void paintComponent(Graphics g) {
            if (isRenderImage()) {
                super.paintComponent(g);
            } else {
                viewer.paintPage((Graphics2D) g.create());
            }
        }

        public boolean isRenderImage() {
            return renderImage;
        }

        public void setRenderImage(boolean renderImage) {
            this.renderImage = renderImage;
        }
    }
    protected javax.swing.JToggleButton btnActualSize;
    protected javax.swing.JButton btnFirst;
    protected javax.swing.JToggleButton btnFitPage;
    protected javax.swing.JToggleButton btnFitWidth;
    protected javax.swing.JButton btnLast;
    protected javax.swing.JButton btnNext;
    protected javax.swing.JButton btnPrevious;
    protected javax.swing.JButton btnPrint;
    protected javax.swing.JButton btnReload;
    protected javax.swing.JButton btnSave;
    protected javax.swing.JButton btnZoomIn;
    protected javax.swing.JButton btnZoomOut;
    protected javax.swing.JComboBox cmbZoom;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JPanel jPanel4;
    protected javax.swing.JPanel jPanel5;
    protected javax.swing.JPanel jPanel6;
    protected javax.swing.JPanel jPanel7;
    protected javax.swing.JPanel jPanel8;
    protected javax.swing.JPanel jPanel9;
    protected PageRenderer lblPage;
    protected javax.swing.JLabel lblStatus;
    protected javax.swing.JPanel pnlInScroll;
    protected javax.swing.JPanel pnlLinks;
    protected javax.swing.JPanel pnlMain;
    protected javax.swing.JPanel pnlPage;
    protected javax.swing.JPanel pnlSep01;
    protected javax.swing.JPanel pnlSep02;
    protected javax.swing.JPanel pnlSep03;
    protected javax.swing.JPanel pnlStatus;
    protected javax.swing.JScrollPane scrollPane;
    protected javax.swing.JPanel tlbToolBar;
    protected javax.swing.JTextField txtGoTo;
}
