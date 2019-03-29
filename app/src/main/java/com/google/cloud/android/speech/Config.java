package com.google.cloud.android.speech;

public enum Config {
    Instance;
    private static String hostUrl = "http://192.168.1.12:8080";

    public static String getSinhalaQuestion = hostUrl + "/restservice/api/questionsservice/newquestion";
    public static String checkSinhalaQuestion = hostUrl + "/restservice/api/questionsservice/getsinhalaquestion";
    public String languageCode="en-US";

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
                this.languageCode = languageCode;
    }

}
