/*
 * LOCKSS Repository Service REST API
 * REST API of the LOCKSS Repository Service
 *
 * The version of the OpenAPI document: 2.0.0
 * Contact: lockss-support@lockss.org
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.lockss.laaws.model.rs;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * Information about a repository storage area
 */
@ApiModel(description = "Information about a repository storage area")
public class StorageInfo {

  private static final long serialVersionUID = 1L;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  private String type;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_SIZE = "size";
  @SerializedName(SERIALIZED_NAME_SIZE)
  private Long size;

  public static final String SERIALIZED_NAME_USED = "used";
  @SerializedName(SERIALIZED_NAME_USED)
  private Long used;

  public static final String SERIALIZED_NAME_AVAIL = "avail";
  @SerializedName(SERIALIZED_NAME_AVAIL)
  private Long avail;

  public static final String SERIALIZED_NAME_PERCENT_USED_STRING = "percentUsedString";
  @SerializedName(SERIALIZED_NAME_PERCENT_USED_STRING)
  private String percentUsedString;

  public static final String SERIALIZED_NAME_PERCENT_USED = "percentUsed";
  @SerializedName(SERIALIZED_NAME_PERCENT_USED)
  private Double percentUsed;


  public StorageInfo type(String type) {

    this.type = type;
    return this;
  }

  /**
   * Type of the storage area
   *
   * @return type
   **/
  @ApiModelProperty(required = true, value = "Type of the storage area")

  public String getType() {
    return type;
  }


  public void setType(String type) {
    this.type = type;
  }


  public StorageInfo name(String name) {

    this.name = name;
    return this;
  }

  /**
   * Name of the storage area
   *
   * @return name
   **/
  @ApiModelProperty(required = true, value = "Name of the storage area")

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public StorageInfo size(Long size) {

    this.size = size;
    return this;
  }

  /**
   * Size in bytes of the storage area
   *
   * @return size
   **/
  @ApiModelProperty(required = true, value = "Size in bytes of the storage area")

  public Long getSize() {
    return size;
  }


  public void setSize(Long size) {
    this.size = size;
  }


  public StorageInfo used(Long used) {

    this.used = used;
    return this;
  }

  /**
   * Used size in bytes of the torage area
   *
   * @return used
   **/
  @ApiModelProperty(required = true, value = "Used size in bytes of the torage area")

  public Long getUsed() {
    return used;
  }


  public void setUsed(Long used) {
    this.used = used;
  }


  public StorageInfo avail(Long avail) {

    this.avail = avail;
    return this;
  }

  /**
   * Available size in bytes of the storage area
   *
   * @return avail
   **/
  @ApiModelProperty(required = true, value = "Available size in bytes of the storage area")

  public Long getAvail() {
    return avail;
  }


  public void setAvail(Long avail) {
    this.avail = avail;
  }


  public StorageInfo percentUsedString(String percentUsedString) {

    this.percentUsedString = percentUsedString;
    return this;
  }

  /**
   * Percentage of size used, formatted as a string
   *
   * @return percentUsedString
   **/
  @ApiModelProperty(required = true, value = "Percentage of size used, formatted as a string")

  public String getPercentUsedString() {
    return percentUsedString;
  }


  public void setPercentUsedString(String percentUsedString) {
    this.percentUsedString = percentUsedString;
  }


  public StorageInfo percentUsed(Double percentUsed) {

    this.percentUsed = percentUsed;
    return this;
  }

  /**
   * Percentage of size used
   *
   * @return percentUsed
   **/
  @ApiModelProperty(required = true, value = "Percentage of size used")

  public Double getPercentUsed() {
    return percentUsed;
  }


  public void setPercentUsed(Double percentUsed) {
    this.percentUsed = percentUsed;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorageInfo storageInfo = (StorageInfo) o;
    return Objects.equals(this.type, storageInfo.type) &&
      Objects.equals(this.name, storageInfo.name) &&
      Objects.equals(this.size, storageInfo.size) &&
      Objects.equals(this.used, storageInfo.used) &&
      Objects.equals(this.avail, storageInfo.avail) &&
      Objects.equals(this.percentUsedString, storageInfo.percentUsedString) &&
      Objects.equals(this.percentUsed, storageInfo.percentUsed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, size, used, avail, percentUsedString, percentUsed);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorageInfo {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    used: ").append(toIndentedString(used)).append("\n");
    sb.append("    avail: ").append(toIndentedString(avail)).append("\n");
    sb.append("    percentUsedString: ").append(toIndentedString(percentUsedString)).append("\n");
    sb.append("    percentUsed: ").append(toIndentedString(percentUsed)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
