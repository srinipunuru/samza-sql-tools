package com.linkedin.samza.tools.schemas;

@SuppressWarnings("all")
public class SimpleRecord extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"SimpleRecord\",\"namespace\":\"org.apache.samza.sql.system.avro\",\"fields\":[{\"name\":\"id\",\"type\":[\"null\",\"int\"],\"doc\":\"Record id.\",\"default\":null},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"doc\":\"Some name.\",\"default\":null}]}");
  /** Record id. */
  public java.lang.Integer id;
  /** Some name. */
  public java.lang.CharSequence name;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
      case 0: return id;
      case 1: return name;
      default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
      case 0: id = (java.lang.Integer)value$; break;
      case 1: name = (java.lang.CharSequence)value$; break;
      default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
