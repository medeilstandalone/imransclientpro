/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.vanuston.medeil.util.plaf;

import java.util.*;
import javax.swing.*;

/**
 * @author Michael Hagen
 */
public class MintLookAndFeel extends AbstractLookAndFeel {

    private static MintDefaultTheme myTheme = null;
    private static ArrayList themesList = new ArrayList();
    private static HashMap themesMap = new HashMap();
    private static Properties defaultProps = new Properties();
    private static Properties smallFontProps = new Properties();
    private static Properties largeFontProps = new Properties();
    private static Properties giantFontProps = new Properties();


    static {
        smallFontProps.setProperty("controlTextFont", "Dialog 10");
        smallFontProps.setProperty("systemTextFont", "Dialog 10");
        smallFontProps.setProperty("userTextFont", "Dialog 10");
        smallFontProps.setProperty("menuTextFont", "Dialog 10");
        smallFontProps.setProperty("windowTitleFont", "Dialog bold 10");
        smallFontProps.setProperty("subTextFont", "Dialog 8");

        largeFontProps.setProperty("controlTextFont", "Dialog 14");
        largeFontProps.setProperty("systemTextFont", "Dialog 14");
        largeFontProps.setProperty("userTextFont", "Dialog 14");
        largeFontProps.setProperty("menuTextFont", "Dialog 14");
        largeFontProps.setProperty("windowTitleFont", "Dialog bold 14");
        largeFontProps.setProperty("subTextFont", "Dialog 12");

        giantFontProps.setProperty("controlTextFont", "Dialog 18");
        giantFontProps.setProperty("systemTextFont", "Dialog 18");
        giantFontProps.setProperty("userTextFont", "Dialog 18");
        giantFontProps.setProperty("menuTextFont", "Dialog 18");
        giantFontProps.setProperty("windowTitleFont", "Dialog 18");
        giantFontProps.setProperty("subTextFont", "Dialog 16");


        themesList.add("Default");
        themesList.add("Small-Font");
        themesList.add("Large-Font");
        themesList.add("Giant-Font");

        themesMap.put("Default", defaultProps);
        themesMap.put("Small-Font", smallFontProps);
        themesMap.put("Large-Font", largeFontProps);
        themesMap.put("Giant-Font", giantFontProps);

    }

    public static java.util.List getThemes() {
        return themesList;
    }

    public static Properties getThemeProperties(String name) {
        return ((Properties) themesMap.get(name));
    }

    public static void setTheme(String name) {
        if (myTheme != null) {
            myTheme.setInternalName(name);
        }
        setTheme((Properties) themesMap.get(name));
    }

    public static void setTheme(String name, String licenseKey, String logoString) {
        Properties props = (Properties) themesMap.get(name);
        props.put("licenseKey", licenseKey);
        props.put("logoString", logoString);
        if (myTheme != null) {
            myTheme.setInternalName(name);
        }
        setTheme(props);
    }

    public static void setTheme(Properties themesProps) {
        if (myTheme == null) {
            myTheme = new MintDefaultTheme();
        }
        if ((myTheme != null) && (themesProps != null)) {
            myTheme.setUpColor();
            myTheme.setProperties(themesProps);
            myTheme.setUpColorArrs();
            AbstractLookAndFeel.setTheme(myTheme);
        }
    }

    public static void setCurrentTheme(Properties themesProps) {
        setTheme(themesProps);
    }

    public String getName() {
        return "Mint";
    }

    public String getID() {
        return "Mint";
    }

    public String getDescription() {
        return "The Mint Look and Feel";
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public AbstractBorderFactory getBorderFactory() {
        return MintBorderFactory.getInstance();
    }

    public AbstractIconFactory getIconFactory() {
        return MintIconFactory.getInstance();
    }

    protected void createDefaultTheme() {
        if (myTheme == null) {
            myTheme = new MintDefaultTheme();
        }
        setTheme(myTheme);
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {
            // BaseLookAndFeel classes
//            "LabelUI", BaseLabelUI.class.getName(),
//            "SeparatorUI", BaseSeparatorUI.class.getName(),
            "RadioButtonUI", BaseRadioButtonUI.class.getName(),
            "CheckBoxUI", BaseCheckBoxUI.class.getName(),
            "TextFieldUI", BaseTextFieldUI.class.getName(),
//            "TextAreaUI", BaseTextAreaUI.class.getName(),
//            "EditorPaneUI", BaseEditorPaneUI.class.getName(),
//            "ToolTipUI", BaseToolTipUI.class.getName(),
//            "TreeUI", BaseTreeUI.class.getName(),
            "TableUI", BaseTableUI.class.getName(),
            "TableHeaderUI", BaseTableHeaderUI.class.getName(),
//            "PanelUI", BasePanelUI.class.getName(),
            "ScrollPaneUI", BaseScrollPaneUI.class.getName(),
//            "ProgressBarUI", BaseProgressBarUI.class.getName(),
            "SliderUI", BaseSliderUI.class.getName(),
//            "SplitPaneUI", BaseSplitPaneUI.class.getName(),
            "FileChooserUI", BaseFileChooserUI.class.getName(),
//            "MenuUI", BaseMenuUI.class.getName(),
//            "PopupMenuUI", BasePopupMenuUI.class.getName(),
            "MenuItemUI", BaseMenuItemUI.class.getName(),
            "CheckBoxMenuItemUI", BaseCheckBoxMenuItemUI.class.getName(),
            "RadioButtonMenuItemUI", BaseRadioButtonMenuItemUI.class.getName(),
//            "PopupMenuSeparatorUI", BaseSeparatorUI.class.getName(),
            // MintLookAndFeel classes
            "ButtonUI", MintButtonUI.class.getName(),
//            "ToggleButtonUI", MintToggleButtonUI.class.getName(),
            "ComboBoxUI", MintComboBoxUI.class.getName(),
//            "TabbedPaneUI", MintTabbedPaneUI.class.getName(),
            "ScrollBarUI", MintScrollBarUI.class.getName(),
//            "ToolBarUI", MintToolBarUI.class.getName(),
//            "MenuBarUI", MintMenuBarUI.class.getName(),
//            "InternalFrameUI", MintInternalFrameUI.class.getName(),
//            "RootPaneUI", MintRootPaneUI.class.getName(),
        };
        table.putDefaults(uiDefaults);
        if (NewClass.getJavaVersion() >= 1.5) {
            table.put("SpinnerUI", BaseSpinnerUI.class.getName());
        }
    }
}