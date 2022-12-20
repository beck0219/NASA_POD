package com.example.nasa_pod;
/**
 * SavedPOTD class is used to store the information of the Picture of the Day.
 *
 */
public class SavedPOTD {
        public int potdID;
        public String potdTitle;
        public String potdDescription;
        public String potdDate;
        public String potdImageUrl;
        public SavedPOTD(String potdTitle, String potdDate, String potdImageUrl, String potdDescription) {
            this.potdTitle = potdTitle;
            this.potdDate = potdDate;
            this.potdImageUrl = potdImageUrl;
            this.potdDescription = potdDescription;
        }
        public SavedPOTD(int potdID, String potdDate, String potdImageUrl ) {
            this.potdID = potdID;
            this.potdDate = potdDate;
            this.potdImageUrl = potdImageUrl;
        }

    public int getPotdID() {
        return potdID;
    }
}

