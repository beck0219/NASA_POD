package com.example.nasa_pod;
/**
 * SavedPOTD class is used to store the information of the Picture of the Day.
 *
 */
public class SavedPOTD {
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
        public SavedPOTD(String potdDate, String potdImageUrl) {
            this.potdDate = potdDate;
            this.potdImageUrl = potdImageUrl;
        }
}

