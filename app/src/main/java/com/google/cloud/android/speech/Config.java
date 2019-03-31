package com.google.cloud.android.speech;

public enum Config {
    Instance;
//    private static String hostUrl = "http://192.168.1.12:8080";
    private static String hostUrl = "http://dev.jgsw.global/sampathsam";

    public static String getSinhalaQuestion = hostUrl + "/restservice/api/questionsservice/newquestion";

    public static String checkEnglishQuestion= hostUrl + "/api/questionsservice/getenglishquestion";
    public static String checkSinhalaQuestion = hostUrl + "/api/questionsservice/getsinhalaquestion";
    public static String checkTamilQuestion = hostUrl + "/api/questionsservice/gettamilquestion";
    public String languageCode="en-US";

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
                this.languageCode = languageCode;
    }

}
