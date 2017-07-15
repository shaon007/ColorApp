package com.simplelifesolution.colorApp;

/**
 * Created by rhsha on 07/04/2017.
 */

public class BeanSimilarColor {

    private String colorType;
    private String colorHexCode;

    public BeanSimilarColor()
    {

    }

    public BeanSimilarColor(String clrType, String clrHex)
    {
        this.colorType = clrType;
        this.colorHexCode = clrHex;
    }

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }

    public String getColorHexCode() {
        return colorHexCode;
    }

    public void setColorHexCode(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }


}
