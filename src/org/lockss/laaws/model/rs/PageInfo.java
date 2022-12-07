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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.lockss.laaws.client.JSON;

/**
 * The information related to pagination of content
 */
@ApiModel(description = "The information related to pagination of content")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PageInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SERIALIZED_NAME_TOTAL_COUNT = "totalCount";
  @SerializedName(SERIALIZED_NAME_TOTAL_COUNT) private Integer totalCount;

  public static final String SERIALIZED_NAME_RESULTS_PER_PAGE = "resultsPerPage";
  @SerializedName(SERIALIZED_NAME_RESULTS_PER_PAGE) private Integer resultsPerPage;

  public static final String SERIALIZED_NAME_CONTINUATION_TOKEN = "continuationToken";
  @SerializedName(SERIALIZED_NAME_CONTINUATION_TOKEN) private String continuationToken;

  public static final String SERIALIZED_NAME_CUR_LINK = "curLink";
  @SerializedName(SERIALIZED_NAME_CUR_LINK) private String curLink;

  public static final String SERIALIZED_NAME_NEXT_LINK = "nextLink";
  @SerializedName(SERIALIZED_NAME_NEXT_LINK) private String nextLink;

  public PageInfo() {}

  public PageInfo totalCount(Integer totalCount) {
    this.totalCount = totalCount;
    return this;
  }

  /**
   * The total number of results
   * @return totalCount
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The total number of results")

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public PageInfo resultsPerPage(Integer resultsPerPage) {
    this.resultsPerPage = resultsPerPage;
    return this;
  }

  /**
   * The number of results per page
   * @return resultsPerPage
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The number of results per page")

  public Integer getResultsPerPage() {
    return resultsPerPage;
  }

  public void setResultsPerPage(Integer resultsPerPage) {
    this.resultsPerPage = resultsPerPage;
  }

  public PageInfo continuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * The continuation token
   * @return continuationToken
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The continuation token")

  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public PageInfo curLink(String curLink) {
    this.curLink = curLink;
    return this;
  }

  /**
   * The link of the current request
   * @return curLink
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The link of the current request")

  public String getCurLink() {
    return curLink;
  }

  public void setCurLink(String curLink) {
    this.curLink = curLink;
  }

  public PageInfo nextLink(String nextLink) {
    this.nextLink = nextLink;
    return this;
  }

  /**
   * The link of the next request
   * @return nextLink
   **/
  @javax.annotation.Nonnull
  @ApiModelProperty(required = true, value = "The link of the next request")

  public String getNextLink() {
    return nextLink;
  }

  public void setNextLink(String nextLink) {
    this.nextLink = nextLink;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageInfo pageInfo = (PageInfo) o;
    return Objects.equals(this.totalCount, pageInfo.totalCount)
        && Objects.equals(this.resultsPerPage, pageInfo.resultsPerPage)
        && Objects.equals(this.continuationToken, pageInfo.continuationToken)
        && Objects.equals(this.curLink, pageInfo.curLink)
        && Objects.equals(this.nextLink, pageInfo.nextLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalCount, resultsPerPage, continuationToken, curLink, nextLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageInfo {\n");
    sb.append("    totalCount: ").append(toIndentedString(totalCount)).append("\n");
    sb.append("    resultsPerPage: ").append(toIndentedString(resultsPerPage)).append("\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    curLink: ").append(toIndentedString(curLink)).append("\n");
    sb.append("    nextLink: ").append(toIndentedString(nextLink)).append("\n");
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
    openapiFields.add("totalCount");
    openapiFields.add("resultsPerPage");
    openapiFields.add("continuationToken");
    openapiFields.add("curLink");
    openapiFields.add("nextLink");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
    openapiRequiredFields.add("totalCount");
    openapiRequiredFields.add("resultsPerPage");
    openapiRequiredFields.add("continuationToken");
    openapiRequiredFields.add("curLink");
    openapiRequiredFields.add("nextLink");
  }

  /**
   * Validates the JSON Object and throws an exception if issues found
   *
   * @param jsonObj JSON Object
   * @throws IOException if the JSON Object is invalid with respect to PageInfo
   */
  public static void validateJsonObject(JsonObject jsonObj) throws IOException {
    if (jsonObj == null) {
      if (PageInfo.openapiRequiredFields.isEmpty()) {
        return;
      } else { // has required fields
        throw new IllegalArgumentException(String.format(
            "The required field(s) %s in PageInfo is not found in the empty JSON string",
            PageInfo.openapiRequiredFields.toString()));
      }
    }

    Set<Entry<String, JsonElement>> entries = jsonObj.entrySet();
    // check to see if the JSON string contains additional fields
    for (Entry<String, JsonElement> entry : entries) {
      if (!PageInfo.openapiFields.contains(entry.getKey())) {
        throw new IllegalArgumentException(String.format(
            "The field `%s` in the JSON string is not defined in the `PageInfo` properties. JSON: %s",
            entry.getKey(), jsonObj.toString()));
      }
    }

    // check to make sure all required properties/fields are present in the JSON string
    for (String requiredField : PageInfo.openapiRequiredFields) {
      if (jsonObj.get(requiredField) == null) {
        throw new IllegalArgumentException(
            String.format("The required field `%s` is not found in the JSON string: %s",
                requiredField, jsonObj.toString()));
      }
    }
    if ((jsonObj.get("continuationToken") != null && !jsonObj.get("continuationToken").isJsonNull())
        && !jsonObj.get("continuationToken").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
          "Expected the field `continuationToken` to be a primitive type in the JSON string but got `%s`",
          jsonObj.get("continuationToken").toString()));
    }
    if ((jsonObj.get("curLink") != null && !jsonObj.get("curLink").isJsonNull())
        && !jsonObj.get("curLink").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
          "Expected the field `curLink` to be a primitive type in the JSON string but got `%s`",
          jsonObj.get("curLink").toString()));
    }
    if ((jsonObj.get("nextLink") != null && !jsonObj.get("nextLink").isJsonNull())
        && !jsonObj.get("nextLink").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
          "Expected the field `nextLink` to be a primitive type in the JSON string but got `%s`",
          jsonObj.get("nextLink").toString()));
    }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      if (!PageInfo.class.isAssignableFrom(type.getRawType())) {
        return null; // this class only serializes 'PageInfo' and its subtypes
      }
      final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
      final TypeAdapter<PageInfo> thisAdapter =
          gson.getDelegateAdapter(this, TypeToken.get(PageInfo.class));

      return (TypeAdapter<T>) new TypeAdapter<PageInfo>() {
        @Override
        public void write(JsonWriter out, PageInfo value) throws IOException {
          JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
          elementAdapter.write(out, obj);
        }

        @Override
        public PageInfo read(JsonReader in) throws IOException {
          JsonObject jsonObj = elementAdapter.read(in).getAsJsonObject();
          validateJsonObject(jsonObj);
          return thisAdapter.fromJsonTree(jsonObj);
        }
      }.nullSafe();
    }
  }

  /**
   * Create an instance of PageInfo given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of PageInfo
   * @throws IOException if the JSON string is invalid with respect to PageInfo
   */
  public static PageInfo fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, PageInfo.class);
  }

  /**
   * Convert an instance of PageInfo to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}
