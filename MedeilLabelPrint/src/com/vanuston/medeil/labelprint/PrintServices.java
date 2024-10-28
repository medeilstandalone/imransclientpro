/*
 *   PROBILZ – Retail Management System (RMS) To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vanuston.medeil.labelprint;

import com.vanuston.medeil.model.LabelPrinterModel;
import com.vanuston.medeil.util.Logger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

public class PrintServices {

    private String printerName;
    Logger log=Logger.getLogger(PrintServices.class,"PrintServices");

    public PrintService getPrintService(String printerName) {
        PrintService psZebra = null;
        try {
           String sPrinterName = null;
           List<String> printers = null;
           PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
           for (int i = 0; i < services.length; i++) {
               PrintServiceAttribute attr = services[i].getAttribute(PrinterName.class);
               sPrinterName = ((PrinterName) attr).getValue();
               if (sPrinterName.indexOf(printerName) >= 0) {
                   psZebra = services[i];
                   break;
               }
           }

           if (psZebra == null) {
               log.debug("Zebra printer not found.");
               return psZebra;
           }
        } catch (Exception e) {
            log.debug("PrintServices  getPrintService Exception:"+e.getMessage() );


        }
    return psZebra;
    }

    public List<String> listPrinter(){
         System.out.println ("------listPrinter");
        List<String> printers = new ArrayList<String>();
        PrintService psZebra = null;
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (int i = 0; i < services.length; i++) {
               PrintServiceAttribute attr = services[i].getAttribute(PrinterName.class);
               printers.add(((PrinterName) attr).getValue());
            }
        return printers;
    }

    public void PrintJob(PrintService ps,LabelPrinterModel pModel) {
         System.out.println ("--------PrintJob");
        DocPrintJob job = ps.createPrintJob();
        String code=pModel.getProductCode();
        if(code != null && code.length() > 2){
            String eplCommandHeader = "N" + "\n"
                    + "R10,0" + "\n"
                    + "Q203,30" + "\n"//height of the label (in dots)
                    ;
            String eplCommandBody =
                      "A0,0,0,3,1,1,N,\"" + pModel.getProductCode() + "\"" + "\n"//first label Barcode
                    + "A0,6,0,3,1,1,N,\"" + pModel.getShopName() + "\"" + "\n"//first label shopname
                    + "A0,24,0,3,1,1,N,\"" + pModel.getProductName() + "\"" + "\n"//first label productname
                    + "A204,46,0,3,1,1,N,\"Rs." + pModel.getSellingPrice() + "\"" + "\n"//first label Price
                    ;
            String eplCommandFooter = "P"+pModel.getPrintCopies()+","+pModel.getPrintSet();
            if(pModel.getPrintSet()==2){
                eplCommandBody = eplCommandBody
                    + "A420,0,0,3,1,1,N,\"" + pModel.getProductCode() + "\"" + "\n" //Second label Barcode
                    + "A420,6,0,3,1,1,N,\"" + pModel.getShopName() + "\"" + "\n"//Second label shopname
                    + "A420,24,0,3,1,1,N,\"" + pModel.getProductName() + "\"" + "\n"//Second label productname
                    + "A625,46,0,3,1,1,N,\"Rs." + pModel.getSellingPrice() + "\"" + "\n"//Second label Price
                    ;
            }
            String eplCommand = eplCommandHeader+eplCommandBody+eplCommandFooter;
            InputStream is = new ByteArrayInputStream(eplCommand.getBytes());
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(is, flavor, null);
            try {
                job.print(doc, null);
            } catch (Exception e) {
                e.printStackTrace();
                log.debug("PrintServices  PrintJob Exception:"+e.getMessage() );
            }
        } else {
            log.debug("PrintServices  PrintJob info: Product Model Null." );
        }
    }
}
