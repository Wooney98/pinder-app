package com.example.pinder99.data;

/**
 * 리사이클러뷰에 뿌리는 아이템
 **/
public class ReportEntity {

        private String report_type;  //구분
        private String locate;
        private String detail_locate;
        private String report_time;
        private String pet_kind;
        private String report_image; //사진
        private String feature;
        private String phoneNum;
        private String Pet_Gender;

        public ReportEntity() {

        }

        public String getReport_type(){return report_type;}

        public String getLocate(){ return locate; }

        public String getDetail_locate() { return detail_locate; }

        public String getReport_time() { return report_time; }

        public String getPet_kind() { return pet_kind; }

        public String getReport_image() {
                return report_image;
        }

        public String getFeature() { return feature; }

        public String getPhoneNum() { return phoneNum; }

        public String getPet_Gender() { return Pet_Gender; }

        public ReportEntity(String report_image, String locate, String report_time, String pet_kind, String detail_locate, String type,
                            String feature, String phoneNum, String Pet_Gender){  //리싸이클러뷰에 뿌려지는 변수들
                this.report_image = report_image;
                this.locate = locate;
                this.report_time=report_time;
                this.pet_kind=pet_kind;
                this.detail_locate=detail_locate;
                this.report_type=type;
                this.feature=feature;
                this.phoneNum=phoneNum;
                this.Pet_Gender=Pet_Gender;

        }

}