package me.zchang.onchart.student;

import android.os.Parcel;
import android.os.Parcelable;

import me.zchang.onchart.config.PreferenceManager;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

public class LabelCourse extends Course implements Parcelable{

    public LabelCourse(Course course) {
        super(course);
    }

    public LabelCourse() {
        super();
    }

    public void setToNextLabelImg() {
        this.labelImgIndex = (this.labelImgIndex + 1) % PreferenceManager.labelImgs.length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.teacher);
        dest.writeString(this.classroom);
        dest.writeInt(this.startWeek);
        dest.writeInt(this.endWeek);
        dest.writeFloat(this.credit);
        dest.writeInt(this.labelImgIndex);
    }

    public static final Parcelable.Creator<Course> CREATOR = new Creator<Course>() {

        @Override
        public Course createFromParcel(Parcel source) {
            Course course = new LabelCourse();
            course.setId(source.readInt());
            course.setName(source.readString());
            course.setTeacher(source.readString());
            course.setClassroom(source.readString());
            course.setStartWeek(source.readInt());
            course.setEndWeek(source.readInt());
            course.setCredit(source.readFloat());
            course.setLabelImgIndex(source.readInt());
            return course;
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[0];
        }
    };
}
