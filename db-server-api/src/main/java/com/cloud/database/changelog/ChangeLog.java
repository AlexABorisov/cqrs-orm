package com.cloud.database.changelog;

import com.cloud.classcache.ClassCache;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 23.11.2015.
 */

@Table("ChangeLog")
public class ChangeLog {
    public static enum Command{
        Create,Update,Delete
    }

    @PrimaryKey
    private ChangelogPK id;

    private String command;

    private Map <String,String> properties = new HashMap<String, String>();


    public ChangeLog() {
    }

    public ChangeLog(ChangelogPK id, String command, Map<String, String> properties) {
        this.id = id;
        this.command = command;
        this.properties = properties;
    }

    public ChangelogPK getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setId(ChangelogPK id) {
        this.id = id;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeLog that = (ChangeLog) o;

        if (command != that.command) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangelogItem{" +
                "id=" + id +
                ", command=" + command +
                ", properties=" + properties +
                '}';
    }
}

