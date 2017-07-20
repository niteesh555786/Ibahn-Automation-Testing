package com.bizbrolly.entities;

/**
 * Created by Akash on 19/05/17.
 */

public class GetDbDetailsResponse {

    /**
     * GetDBDetailsResult : {"Data":{"__type":"clsdetails:#Svarochi.Model","BulbId":"BLB001","DBScript":"{\n    Device =     (\n                {\n            appearanceShortname = SUJBTi1SR0JX;\n            appearanceValue = 4192;\n            deviceHash = \"Sx3nbQ==\";\n            dhmKey = \"BvDSgpS9W1X8lte/FIGkuPP+xl3B8mWJ\";\n            id = 32769;\n            name = \"Rachit LED\";\n            type = RGBW;\n        }\n    );\n    NetworkKey = \"biz@123\";\n}","Email":"rachit.gupta@bizbrolly.com","Id":113,"NetworkPassword":"biz@123"},"ErrorDetail":{"ErrorDetails":"","ErrorMessage":""},"Result":true}
     */

    private GetDBDetailsResultEntity GetDBDetailsResult;

    public void setGetDBDetailsResult(GetDBDetailsResultEntity GetDBDetailsResult) {
        this.GetDBDetailsResult = GetDBDetailsResult;
    }

    public GetDBDetailsResultEntity getGetDBDetailsResult() {
        return GetDBDetailsResult;
    }

    public static class GetDBDetailsResultEntity {
        /**
         * Data : {"__type":"clsdetails:#Svarochi.Model","BulbId":"BLB001","DBScript":"{\n    Device =     (\n                {\n            appearanceShortname = SUJBTi1SR0JX;\n            appearanceValue = 4192;\n            deviceHash = \"Sx3nbQ==\";\n            dhmKey = \"BvDSgpS9W1X8lte/FIGkuPP+xl3B8mWJ\";\n            id = 32769;\n            name = \"Rachit LED\";\n            type = RGBW;\n        }\n    );\n    NetworkKey = \"biz@123\";\n}","Email":"rachit.gupta@bizbrolly.com","Id":113,"NetworkPassword":"biz@123"}
         * ErrorDetail : {"ErrorDetails":"","ErrorMessage":""}
         * Result : true
         */

        private DataEntity Data;
        private ErrorDetailEntity ErrorDetail;
        private boolean Result;

        public void setData(DataEntity Data) {
            this.Data = Data;
        }

        public void setErrorDetail(ErrorDetailEntity ErrorDetail) {
            this.ErrorDetail = ErrorDetail;
        }

        public void setResult(boolean Result) {
            this.Result = Result;
        }

        public DataEntity getData() {
            return Data;
        }

        public ErrorDetailEntity getErrorDetail() {
            return ErrorDetail;
        }

        public boolean getResult() {
            return Result;
        }

        public static class DataEntity {
            /**
             * __type : clsdetails:#Svarochi.Model
             * BulbId : BLB001
             * DBScript : {
             Device =     (
             {
             appearanceShortname = SUJBTi1SR0JX;
             appearanceValue = 4192;
             deviceHash = "Sx3nbQ==";
             dhmKey = "BvDSgpS9W1X8lte/FIGkuPP+xl3B8mWJ";
             id = 32769;
             name = "Rachit LED";
             type = RGBW;
             }
             );
             NetworkKey = "biz@123";
             }
             * Email : rachit.gupta@bizbrolly.com
             * Id : 113
             * NetworkPassword : biz@123
             */

            private String __type;
            private String BulbId;
            private String DBScript;
            private String Email;
            private int Id;
            private String NetworkPassword;

            public void set__type(String __type) {
                this.__type = __type;
            }

            public void setBulbId(String BulbId) {
                this.BulbId = BulbId;
            }

            public void setDBScript(String DBScript) {
                this.DBScript = DBScript;
            }

            public void setEmail(String Email) {
                this.Email = Email;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public void setNetworkPassword(String NetworkPassword) {
                this.NetworkPassword = NetworkPassword;
            }

            public String get__type() {
                return __type;
            }

            public String getBulbId() {
                return BulbId;
            }

            public String getDBScript() {
                return DBScript;
            }

            public String getEmail() {
                return Email;
            }

            public int getId() {
                return Id;
            }

            public String getNetworkPassword() {
                return NetworkPassword;
            }
        }

        public static class ErrorDetailEntity {
            /**
             * ErrorDetails :
             * ErrorMessage :
             */

            private String ErrorDetails;
            private String ErrorMessage;

            public void setErrorDetails(String ErrorDetails) {
                this.ErrorDetails = ErrorDetails;
            }

            public void setErrorMessage(String ErrorMessage) {
                this.ErrorMessage = ErrorMessage;
            }

            public String getErrorDetails() {
                return ErrorDetails;
            }

            public String getErrorMessage() {
                return ErrorMessage;
            }
        }
    }
}
