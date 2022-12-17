package com.example.nasa_pod;
/**
 * This class represents a Picture of the Day (POTD) object.
 *
 * @author [author name]
 */
public class POTD {
    public String potdTitle;
    public String potdDescription;
    public String potdDate;
    public String potdImageUrl;
    public POTD(String potdTitle, String potdDate, String potdImageUrl, String potdDescription) {
        this.potdTitle = potdTitle;
        this.potdDate = potdDate;
        this.potdImageUrl = potdImageUrl;
        this.potdDescription = potdDescription;
    }

    public POTD(String potdDate, String potdImageUrl) {
        this.potdDate = potdDate;
        this.potdImageUrl = potdImageUrl;
    }
}
