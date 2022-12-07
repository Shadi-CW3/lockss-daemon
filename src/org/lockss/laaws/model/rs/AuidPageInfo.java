/*
 * Copyright (c) 2000-2022, Board of Trustees of Leland Stanford Jr. University
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
*/

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.lockss.laaws.client.JSON;
import org.lockss.laaws.model.rs.PageInfo;

/**
 * A display page of Archival Unit identifiers
 */
@ApiModel(description = "A display page of Archival Unit identifiers")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class AuidPageInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SERIALIZED_NAME_AUIDS = "auids";
  @SerializedName(SERIALIZED_NAME_AUIDS) private List<String> auids = new ArrayList<>();

  public static final String SERIALIZED_NAME_PAGE_INFO = "pageInfo";
  @SerializedName(SERIALIZED_NAME_PAGE_INFO) private PageInfo pageInfo;

  public AuidPageInfo() {}

  public AuidPageInfo auids(List<String> auids) {
    this.auids = auids;
    return this;
  }

  public AuidPageInfo addAuidsItem(String auidsItem) {
    this.auids.add(auidsItem);
    return this;
  }

  /**
   * The Archival Unit identifiers included in the page
   * @return auids
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The Archival Unit identifiers included in the page")

  public List<String> getAuids() {
    return auids;
  }

  public void setAuids(List<String> auids) {
    this.auids = auids;
  }

  public AuidPageInfo pageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
    return this;
  }

  /**
   * Get pageInfo
   * @return pageInfo
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "")

  public PageInfo getPageInfo() {
    return pageInfo;
  }

  public void setPageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuidPageInfo auidPageInfo = (AuidPageInfo) o;
    return Objects.equals(this.auids, auidPageInfo.auids)
        && Objects.equals(this.pageInfo, auidPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auids, pageInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuidPageInfo {\n");
    sb.append("    auids: ").append(toIndentedString(auids)).append("\n");
    sb.append("    pageInfo: ").append(toIndentedString(pageInfo)).append("\n");
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

  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("auids");
    openapiFields.add("pageInfo");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("auids");
    openapiRequiredFields.add("pageInfo");
  }

  /**
   * Validates the JSON Object and throws an exception if issues found
   *
   * @param jsonObj JSON Object
   * @throws IOException if the JSON Object is invalid with respect to AuidPageInfo
   */
  public static void validateJsonObject(JsonObject jsonObj) throws IOException {
    if (jsonObj == null) {
      if (AuidPageInfo.openapiRequiredFields.isEmpty()) {
        return;
      } else { // has required fields
        throw new IllegalArgumentException(String.format(
            "The required field(s) %s in AuidPageInfo is not found in the empty JSON string",
            AuidPageInfo.openapiRequiredFields.toString()));
      }
    }

    Set<Entry<String, JsonElement>> entries = jsonObj.entrySet();
    // check to see if the JSON string contains additional fields
    for (Entry<String, JsonElement> entry : entries) {
      if (!AuidPageInfo.openapiFields.contains(entry.getKey())) {
        throw new IllegalArgumentException(String.format(
            "The field `%s` in the JSON string is not defined in the `AuidPageInfo` properties. JSON: %s",
            entry.getKey(), jsonObj.toString()));
      }
    }

    // check to make sure all required properties/fields are present in the JSON string
    for (String requiredField : AuidPageInfo.openapiRequiredFields) {
      if (jsonObj.get(requiredField) == null) {
        throw new IllegalArgumentException(
            String.format("The required field `%s` is not found in the JSON string: %s",
                requiredField, jsonObj.toString()));
      }
    }
    // ensure the json data is an array
    if ((jsonObj.get("auids") != null && !jsonObj.get("auids").isJsonNull())
        && !jsonObj.get("auids").isJsonArray()) {
      throw new IllegalArgumentException(
          String.format("Expected the field `auids` to be an array in the JSON string but got `%s`",
              jsonObj.get("auids").toString()));
    }
    // validate the optional field `pageInfo`
    if (jsonObj.get("pageInfo") != null && !jsonObj.get("pageInfo").isJsonNull()) {
      PageInfo.validateJsonObject(jsonObj.getAsJsonObject("pageInfo"));
    }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      if (!AuidPageInfo.class.isAssignableFrom(type.getRawType())) {
        return null; // this class only serializes 'AuidPageInfo' and its subtypes
      }
      final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
      final TypeAdapter<AuidPageInfo> thisAdapter =
          gson.getDelegateAdapter(this, TypeToken.get(AuidPageInfo.class));

      return (TypeAdapter<T>) new TypeAdapter<AuidPageInfo>() {
        @Override
        public void write(JsonWriter out, AuidPageInfo value) throws IOException {
          JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
          elementAdapter.write(out, obj);
        }

        @Override
        public AuidPageInfo read(JsonReader in) throws IOException {
          JsonObject jsonObj = elementAdapter.read(in).getAsJsonObject();
          validateJsonObject(jsonObj);
          return thisAdapter.fromJsonTree(jsonObj);
        }
      }.nullSafe();
    }
  }

  /**
   * Create an instance of AuidPageInfo given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of AuidPageInfo
   * @throws IOException if the JSON string is invalid with respect to AuidPageInfo
   */
  public static AuidPageInfo fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, AuidPageInfo.class);
  }

  /**
   * Convert an instance of AuidPageInfo to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}
