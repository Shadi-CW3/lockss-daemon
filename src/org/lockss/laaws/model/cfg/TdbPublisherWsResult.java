/*
 * LOCKSS Configuration Service REST API
 * REST API of the LOCKSS Configuration Service
 *
 * The version of the OpenAPI document: 2.0.0
 * Contact: lockss-support@lockss.org
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.lockss.laaws.model.cfg;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * The properties of a TDB Publisher
 */
@ApiModel(description = "The properties of a TDB Publisher")
public class TdbPublisherWsResult {

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;


  public TdbPublisherWsResult name(String name) {

    this.name = name;
    return this;
  }

  /**
   * The name of the TDB Publisher
   *
   * @return name
   **/
  @ApiModelProperty(required = true, value = "The name of the TDB Publisher")

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TdbPublisherWsResult tdbPublisherWsResult = (TdbPublisherWsResult) o;
    return Objects.equals(this.name, tdbPublisherWsResult.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TdbPublisherWsResult {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

