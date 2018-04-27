package com.cassandra.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by albo1013 on 17.12.2015.
 */
public class ProxyFactoryCreatorTest {
    public static class Pojo{
        private int id;
        private int primitive;

        private OtherPojo complexType;

        public int getPrimitive() {
            return primitive;
        }

        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }

        public OtherPojo getComplexType() {
            return complexType;
        }

        public void setComplexType(OtherPojo complexType) {
            this.complexType = complexType;
        }

        public Pojo() {
            id = 1;
            primitive = 10;
            complexType = new OtherPojo();

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pojo pojo = (Pojo) o;

            if (id != pojo.id) return false;
            if (primitive != pojo.primitive) return false;
            if (complexType != null ? !complexType.equals(pojo.complexType) : pojo.complexType != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + primitive;
            result = 31 * result + (complexType != null ? complexType.hashCode() : 0);
            return result;
        }
    }

    public static class OtherPojo{
        private int id;
        private long primitive ;

        public OtherPojo() {
            id = 12;
            primitive = 2L;
        }

        public long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(long primitive) {
            this.primitive = primitive;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OtherPojo otherPojo = (OtherPojo) o;

            if (id != otherPojo.id) return false;
            if (primitive != otherPojo.primitive) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (int) (primitive ^ (primitive >>> 32));
            return result;
        }

        public int getId() {

            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    @Test
    public void testCreate(){
//        Pojo pojo = new Pojo();
//        PersistenceCapable proxy = ProxyFactoryCreator.createProxy(pojo, new PersistenceCapableHandler(pojo));
//        Assert.assertTrue(proxy.equals(pojo));
    }
}
