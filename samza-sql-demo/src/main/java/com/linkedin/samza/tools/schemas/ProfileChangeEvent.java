/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.linkedin.samza.tools.schemas;

@SuppressWarnings("all")
public class ProfileChangeEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"ProfileChangeEvent\",\"namespace\":\"com.linkedin.samza.tools.avro\",\"fields\":[{\"name\":\"Name\",\"type\":[\"null\",\"string\"],\"doc\":\"Name of the profile.\",\"default\":null},{\"name\":\"NewCompany\",\"type\":[\"null\",\"string\"],\"doc\":\"Name of the new company the person joined.\",\"default\":null},{\"name\":\"OldCompany\",\"type\":[\"null\",\"string\"],\"doc\":\"Name of the old company the person was working.\",\"default\":null},{\"name\":\"ProfileChangeTimestamp\",\"type\":[\"null\",\"long\"],\"doc\":\"Time at which the profile was changed.\",\"default\":null}]}");
  /** Name of the profile. */
  public java.lang.CharSequence Name;
  /** Name of the new company the person joined. */
  public java.lang.CharSequence NewCompany;
  /** Name of the old company the person was working. */
  public java.lang.CharSequence OldCompany;
  /** Time at which the profile was changed. */
  public java.lang.Long ProfileChangeTimestamp;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return Name;
    case 1: return NewCompany;
    case 2: return OldCompany;
    case 3: return ProfileChangeTimestamp;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: Name = (java.lang.CharSequence)value$; break;
    case 1: NewCompany = (java.lang.CharSequence)value$; break;
    case 2: OldCompany = (java.lang.CharSequence)value$; break;
    case 3: ProfileChangeTimestamp = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
