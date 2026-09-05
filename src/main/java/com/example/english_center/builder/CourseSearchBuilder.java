package com.example.english_center.builder;

import com.example.english_center.enums.CourseStatus;

public class CourseSearchBuilder {

    private String keyword;
    private CourseStatus status;

    public String getKeyword() {
        return keyword;
    }

    public CourseStatus getStatus() {
        return status;
    }

    private CourseSearchBuilder(Builder builder) {
        this.keyword = builder.keyword;
        this.status = builder.status;
    }

    public static class Builder {
        private String keyword;
        private CourseStatus status;

        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setStatus(CourseStatus status) {
            this.status = status;
            return this;
        }

        public CourseSearchBuilder build() {
            return new CourseSearchBuilder(this);
        }
    }
}
