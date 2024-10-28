/*
 *   PROBILZ – Retail Management System (RMS) To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vanuston.medeil.model;

import java.io.Serializable;

/**
 *
 * @author saran
 */
public class LabelPrinterModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String printer;
    private String shopName, shopAddress2, shopAddress3, shopCity, shopState, shopZIP, shopMobilNo;
    private String shopAddress1;
    private String productName;
    private String productCode;
    private String productMrp;
    private String sellingPrice;
    private String content;
    private String productPacked;
    private String bestBeforeOn;
    private Integer printCopies;
    private Integer printSet;
    private String customerName;
    private String customerAddress;
    private String customerCity;
    private String federalCaution;
    private Integer prescreptionNO;
    private String QTY;
    private String PONo;
    private String drugCaution;
    private String age;
    private String sex;
    private String doctorName;
    private String morning;
    private String noon;
    private String evening;
    private String night;
    public static String RX = "Rx";

    private Boolean enableShopName;
    private Boolean enableProductName;
    private Boolean enableProductMrp;
    private Boolean enableSellingPrice;
    private Boolean enableProductPacked;
    private Boolean enableBestBeforeOn;

    private String labelWidth;
    private String labelHeight;
    private String printSpeed;
    private String Density;
    private String sensor;
    private String verticalGap;
    private String shiftOffset;
    private String xPoint;
    private String yPoint;
    private String rotation;
    private String xMagnify;
    private String yMagnify;
    private String barType;
    private String barHeight;
    private String humanInterprate;
    private String narrowRatio1;
    private String narrowRatio2;
    private String fontType;

    private Double barLineX;
    private Double barLineY;
    private Double barLineWidth;
    private Double barLineHeight;

    private String colour;

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getNight() {
        return night;
    }

    public void setNight(String night) {
        this.night = night;
    }

    public String getNoon() {
        return noon;
    }

    public void setNoon(String noon) {
        this.noon = noon;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getShopAddress1() {
        return shopAddress1;
    }

    public void setShopAddress1(String shopAddress1) {
        this.shopAddress1 = shopAddress1;
    }

    public String getShopAddress2() {
        return shopAddress2;
    }

    public void setShopAddress2(String shopAddress2) {
        this.shopAddress2 = shopAddress2;
    }

    public String getShopAddress3() {
        return shopAddress3;
    }

    public void setShopAddress3(String shopAddress3) {
        this.shopAddress3 = shopAddress3;
    }

    public String getShopCity() {
        return shopCity;
    }

    public void setShopCity(String shopCity) {
        this.shopCity = shopCity;
    }

    public String getShopMobilNo() {
        return shopMobilNo;
    }

    public void setShopMobilNo(String shopMobilNo) {
        this.shopMobilNo = shopMobilNo;
    }

    public String getShopState() {
        return shopState;
    }

    public void setShopState(String shopState) {
        this.shopState = shopState;
    }

    public String getShopZIP() {
        return shopZIP;
    }

    public void setShopZIP(String shopZIP) {
        this.shopZIP = shopZIP;
    }
    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public String getPONo() {
        return PONo;
    }

    public void setPONo(String PONo) {
        this.PONo = PONo;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Double getBarLineX() {
        return barLineX;
    }

    public void setBarLineX(Double barLineX) {
        this.barLineX = barLineX;
    }

    public Double getBarLineY() {
        return barLineY;
    }

    public void setBarLineY(Double barLineY) {
        this.barLineY = barLineY;
    }

    public Double getBarLineHeight() {
        return barLineHeight;
    }

    public void setBarLineHeight(Double barLineHeight) {
        this.barLineHeight = barLineHeight;
    }

    public Double getBarLineWidth() {
        return barLineWidth;
    }

    public void setBarLineWidth(Double barLineWidth) {
        this.barLineWidth = barLineWidth;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }



    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }



    public String getDensity() {
        return Density;
    }

    public void setDensity(String Density) {
        this.Density = Density;
    }

    public String getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(String barHeight) {
        this.barHeight = barHeight;
    }

    public String getBarType() {
        return barType;
    }

    public void setBarType(String barType) {
        this.barType = barType;
    }

    public String getHumanInterprate() {
        return humanInterprate;
    }

    public void setHumanInterprate(String humanInterprate) {
        this.humanInterprate = humanInterprate;
    }

    public String getLabelHeight() {
        return labelHeight;
    }

    public void setLabelHeight(String labelHeight) {
        this.labelHeight = labelHeight;
    }

    public String getLabelWidth() {
        return labelWidth;
    }

    public void setLabelWidth(String labelWidth) {
        this.labelWidth = labelWidth;
    }

    public String getNarrowRatio1() {
        return narrowRatio1;
    }

    public void setNarrowRatio1(String narrowRatio1) {
        this.narrowRatio1 = narrowRatio1;
    }

    public String getNarrowRatio2() {
        return narrowRatio2;
    }

    public void setNarrowRatio2(String narrowRatio2) {
        this.narrowRatio2 = narrowRatio2;
    }

    public String getPrintSpeed() {
        return printSpeed;
    }

    public void setPrintSpeed(String printSpeed) {
        this.printSpeed = printSpeed;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String getShiftOffset() {
        return shiftOffset;
    }

    public void setShiftOffset(String shiftOffset) {
        this.shiftOffset = shiftOffset;
    }

    public String getVerticalGap() {
        return verticalGap;
    }

    public void setVerticalGap(String verticalGap) {
        this.verticalGap = verticalGap;
    }

    public String getxMagnify() {
        return xMagnify;
    }

    public void setxMagnify(String xMagnify) {
        this.xMagnify = xMagnify;
    }

    public String getxPoint() {
        return xPoint;
    }

    public void setxPoint(String xPoint) {
        this.xPoint = xPoint;
    }

    public String getyMagnify() {
        return yMagnify;
    }

    public void setyMagnify(String yMagnify) {
        this.yMagnify = yMagnify;
    }

    public String getyPoint() {
        return yPoint;
    }

    public void setyPoint(String yPoint) {
        this.yPoint = yPoint;
    }





    public Integer getPrintCopies() {
        return printCopies;
    }

    public void setPrintCopies(Integer printCopies) {
        this.printCopies = printCopies;
    }

    public Integer getPrintSet() {
        return printSet;
    }

    public void setPrintSet(Integer printSet) {
        this.printSet = printSet;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(String productMrp) {
        this.productMrp = productMrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPacked() {
        return productPacked;
    }

    public void setProductPacked(String productPacked) {
        this.productPacked = productPacked;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Boolean getEnableProductMrp() {
        return enableProductMrp;
    }

    public void setEnableProductMrp(Boolean enableProductMrp) {
        this.enableProductMrp = enableProductMrp;
    }



    public Boolean getEnableProductName() {
        return enableProductName;
    }

    public void setEnableProductName(Boolean enableProductName) {
        this.enableProductName = enableProductName;
    }

    public Boolean getEnableProductPacked() {
        return enableProductPacked;
    }

    public void setEnableProductPacked(Boolean enableProductPacked) {
        this.enableProductPacked = enableProductPacked;
    }

    public Boolean getEnableBestBeforeOn() {
        return enableBestBeforeOn;
    }

    public void setEnableBestBeforeOn(Boolean enableBestBeforeOn) {
        this.enableBestBeforeOn = enableBestBeforeOn;
    }
    
    public Boolean getEnableShopName() {
        return enableShopName;
    }

    public void setEnableShopName(Boolean enableShopName) {
        this.enableShopName = enableShopName;
    }

    public Boolean getEnableSellingPrice() {
        return enableSellingPrice;
    }

    public void setEnableSellingPrice(Boolean enableSellingPrice) {
        this.enableSellingPrice = enableSellingPrice;
    }

    public String getBestBeforeOn() {
        return bestBeforeOn;
    }

    public void setBestBeforeOn(String bestBeforeOn) {
        this.bestBeforeOn = bestBeforeOn;
    }

        public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public Integer getPrescreptionNO() {
        return prescreptionNO;
    }

    public void setPrescreptionNO(Integer prescreptionNO) {
        this.prescreptionNO = prescreptionNO;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFederalCaution() {
        return federalCaution;
    }

    public void setFederalCaution(String federalCaution) {
        this.federalCaution = federalCaution;
    }

    public String getDrugCaution() {
        return drugCaution;
    }

    public void setDrugCaution(String drugCaution) {
        this.drugCaution = drugCaution;
    }
    

}
