package com.bizbrolly.entities;

/**
 * Created by Akash on 26/06/17.
 */

public class AddDetailsResponse {

    /**
     * Data : true
     * ErrorDetail : {"ErrorDetails":"","ErrorMessage":""}
     * Result : true
     */

    private AddDetailsResultBean AddDetailsResult;

    public AddDetailsResultBean getAddDetailsResult() {
        return AddDetailsResult;
    }

    public void setAddDetailsResult(AddDetailsResultBean AddDetailsResult) {
        this.AddDetailsResult = AddDetailsResult;
    }

    public static class AddDetailsResultBean {
        private boolean Data;
        /**
         * ErrorDetails :
         * ErrorMessage :
         */

        private ErrorDetailBean ErrorDetail;
        private boolean Result;

        public boolean isData() {
            return Data;
        }

        public void setData(boolean Data) {
            this.Data = Data;
        }

        public ErrorDetailBean getErrorDetail() {
            return ErrorDetail;
        }

        public void setErrorDetail(ErrorDetailBean ErrorDetail) {
            this.ErrorDetail = ErrorDetail;
        }

        public boolean isResult() {
            return Result;
        }

        public void setResult(boolean Result) {
            this.Result = Result;
        }

        public static class ErrorDetailBean {
            private String ErrorDetails;
            private String ErrorMessage;

            public String getErrorDetails() {
                return ErrorDetails;
            }

            public void setErrorDetails(String ErrorDetails) {
                this.ErrorDetails = ErrorDetails;
            }

            public String getErrorMessage() {
                return ErrorMessage;
            }

            public void setErrorMessage(String ErrorMessage) {
                this.ErrorMessage = ErrorMessage;
            }
        }
    }
}
