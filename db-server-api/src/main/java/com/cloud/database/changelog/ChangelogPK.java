package com.cloud.database.changelog;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by albo1013 on 24.11.2015.
 */
@PrimaryKeyClass
public class ChangelogPK implements Serializable {
    @PrimaryKeyColumn(name = "time_stamp",ordering = Ordering.DESCENDING,ordinal = 0,type = PrimaryKeyType.CLUSTERED)
    private Date time_stamp;

    @PrimaryKeyColumn(name = "id" , ordinal = 1,type = PrimaryKeyType.PARTITIONED)
    private Integer id;

    @PrimaryKeyColumn(name = "type" , ordinal = 3,type = PrimaryKeyType.PARTITIONED)
    private String type;

    public ChangelogPK() {
    }

    public ChangelogPK(Date time_stamp, Integer id, String type) {
        this.time_stamp = time_stamp;
        this.id = id;
        this.type = type;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangelogPK pk = (ChangelogPK) o;

        if (id != null ? !id.equals(pk.id) : pk.id != null) return false;
        if (time_stamp != null ? !time_stamp.equals(pk.time_stamp) : pk.time_stamp != null) return false;
        if (type != null ? !type.equals(pk.type) : pk.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = time_stamp != null ? time_stamp.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangelogPK{" +
                "time_stamp=" + time_stamp +
                ", id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
