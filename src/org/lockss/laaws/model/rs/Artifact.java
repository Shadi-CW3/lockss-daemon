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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.lockss.laaws.client.JSON;

/**
 * Artifact
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class Artifact implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SERIALIZED_NAME_COMMITTED = "committed";
  @SerializedName(SERIALIZED_NAME_COMMITTED) private Boolean committed;

  public static final String SERIALIZED_NAME_STORAGE_URL = "storageUrl";
  @SerializedName(SERIALIZED_NAME_STORAGE_URL) private String storageUrl;

  public static final String SERIALIZED_NAME_NAMESPACE = "namespace";
  @SerializedName(SERIALIZED_NAME_NAMESPACE) private String namespace = "lockss";

  public static final String SERIALIZED_NAME_UUID = "uuid";
  @SerializedName(SERIALIZED_NAME_UUID) private String uuid;

  public static final String SERIALIZED_NAME_AUID = "auid";
  @SerializedName(SERIALIZED_NAME_AUID) private String auid;

  public static final String SERIALIZED_NAME_URI = "uri";
  @SerializedName(SERIALIZED_NAME_URI) private String uri;

  public static final String SERIALIZED_NAME_VERSION = "version";
  @SerializedName(SERIALIZED_NAME_VERSION) private Integer version;

  public static final String SERIALIZED_NAME_CONTENT_LENGTH = "contentLength";
  @SerializedName(SERIALIZED_NAME_CONTENT_LENGTH) private Long contentLength;

  public static final String SERIALIZED_NAME_CONTENT_DIGEST = "contentDigest";
  @SerializedName(SERIALIZED_NAME_CONTENT_DIGEST) private String contentDigest;

  public static final String SERIALIZED_NAME_COLLECTION_DATE = "collectionDate";
  @SerializedName(SERIALIZED_NAME_COLLECTION_DATE) private Long collectionDate;

  public static final String SERIALIZED_NAME_STATE = "state";
  @SerializedName(SERIALIZED_NAME_STATE) private String state;

  public static final String SERIALIZED_NAME_SORTURI = "sortUri";
  @SerializedName(SERIALIZED_NAME_SORTURI) private String sortUri;

  public Artifact() {}

  public Artifact committed(Boolean committed) {
    this.committed = committed;
    return this;
  }

  /**
   * Get committed
   * @return committed
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public Boolean getCommitted() {
    return committed;
  }

  public void setCommitted(Boolean committed) {
    this.committed = committed;
  }

  public Artifact storageUrl(String storageUrl) {
    this.storageUrl = storageUrl;
    return this;
  }

  /**
   * Get storageUrl
   * @return storageUrl
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getStorageUrl() {
    return storageUrl;
  }

  public void setStorageUrl(String storageUrl) {
    this.storageUrl = storageUrl;
  }

  public Artifact namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  /**
   * Get namespace
   * @return namespace
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public Artifact uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * Get uuid
   * @return uuid
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Artifact auid(String auid) {
    this.auid = auid;
    return this;
  }

  /**
   * Get auid
   * @return auid
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getAuid() {
    return auid;
  }

  public void setAuid(String auid) {
    this.auid = auid;
  }

  public Artifact uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public Artifact version(Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Artifact contentLength(Long contentLength) {
    this.contentLength = contentLength;
    return this;
  }

  /**
   * Get contentLength
   * @return contentLength
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public Long getContentLength() {
    return contentLength;
  }

  public void setContentLength(Long contentLength) {
    this.contentLength = contentLength;
  }

  public Artifact contentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
    return this;
  }

  /**
   * Get contentDigest
   * @return contentDigest
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getContentDigest() {
    return contentDigest;
  }

  public void setContentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
  }

  public Artifact collectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
    return this;
  }

  /**
   * Get collectionDate
   * @return collectionDate
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public Long getCollectionDate() {
    return collectionDate;
  }

  public void setCollectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
  }

  public Artifact state(String state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Artifact artifact = (Artifact) o;
    return Objects.equals(this.committed, artifact.committed)
      && Objects.equals(this.storageUrl, artifact.storageUrl)
      && Objects.equals(this.namespace, artifact.namespace)
      && Objects.equals(this.uuid, artifact.uuid) && Objects.equals(this.auid, artifact.auid)
      && Objects.equals(this.uri, artifact.uri) && Objects.equals(this.version, artifact.version)
      && Objects.equals(this.contentLength, artifact.contentLength)
      && Objects.equals(this.contentDigest, artifact.contentDigest)
      && Objects.equals(this.collectionDate, artifact.collectionDate)
      && Objects.equals(this.state, artifact.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(committed, storageUrl, namespace, uuid, auid, uri, version, contentLength,
      contentDigest, collectionDate, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Artifact {\n");
    sb.append("    committed: ").append(toIndentedString(committed)).append("\n");
    sb.append("    storageUrl: ").append(toIndentedString(storageUrl)).append("\n");
    sb.append("    namespace: ").append(toIndentedString(namespace)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    auid: ").append(toIndentedString(auid)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    contentLength: ").append(toIndentedString(contentLength)).append("\n");
    sb.append("    contentDigest: ").append(toIndentedString(contentDigest)).append("\n");
    sb.append("    collectionDate: ").append(toIndentedString(collectionDate)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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
    openapiFields.add("namespace");
    openapiFields.add("uuid");
    openapiFields.add("auid");
    openapiFields.add("uri");
    openapiFields.add("version");
    openapiFields.add("committed"); // may be returned, but not in api
    openapiFields.add("storageUrl"); // may be returned, but not in api
    openapiFields.add("contentLength");
    openapiFields.add("contentDigest");
    openapiFields.add("collectionDate");
    openapiFields.add("state");
    openapiFields.add("sortUri"); // may be returned, but not in api
    openapiFields.add("identifier"); // is this still needed?

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

  /**
   * Validates the JSON Object and throws an exception if issues found
   *
   * @param jsonObj JSON Object
   * @throws IOException if the JSON Object is invalid with respect to Artifact
   */
  public static void validateJsonObject(JsonObject jsonObj) throws IOException {
    if (jsonObj == null) {
      if (!Artifact.openapiRequiredFields
        .isEmpty()) { // has required fields but JSON object is null
        throw new IllegalArgumentException(String.format(
          "The required field(s) %s in Artifact is not found in the empty JSON string",
          Artifact.openapiRequiredFields.toString()));
      }
    }

    Set<Entry<String, JsonElement>> entries = jsonObj.entrySet();
    // check to see if the JSON string contains additional fields
    for (Entry<String, JsonElement> entry : entries) {
      if (!Artifact.openapiFields.contains(entry.getKey())) {
        throw new IllegalArgumentException(String.format(
          "The field `%s` in the JSON string is not defined in the `Artifact` properties. JSON: %s",
          entry.getKey(), jsonObj.toString()));
      }
    }
    if ((jsonObj.get("storageUrl") != null && !jsonObj.get("storageUrl").isJsonNull())
      && !jsonObj.get("storageUrl").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `storageUrl` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("storageUrl").toString()));
    }
    if ((jsonObj.get("namespace") != null && !jsonObj.get("namespace").isJsonNull())
      && !jsonObj.get("namespace").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `namespace` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("namespace").toString()));
    }
    if ((jsonObj.get("uuid") != null && !jsonObj.get("uuid").isJsonNull())
      && !jsonObj.get("uuid").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `uuid` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("uuid").toString()));
    }
    if ((jsonObj.get("auid") != null && !jsonObj.get("auid").isJsonNull())
      && !jsonObj.get("auid").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `auid` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("auid").toString()));
    }
    if ((jsonObj.get("uri") != null && !jsonObj.get("uri").isJsonNull())
      && !jsonObj.get("uri").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `uri` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("uri").toString()));
    }
    // this field is returned but not defined in the api
    if ((jsonObj.get("storageUrl") != null && !jsonObj.get("storageUrl").isJsonNull())
        && !jsonObj.get("storageUrl").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
          "Expected the field `storageUrl` to be a primitive type in the JSON string but got `%s`",
          jsonObj.get("storageUrl").toString()));
    }
    if ((jsonObj.get("contentDigest") != null && !jsonObj.get("contentDigest").isJsonNull())
      && !jsonObj.get("contentDigest").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `contentDigest` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("contentDigest").toString()));
    }
    if ((jsonObj.get("state") != null && !jsonObj.get("state").isJsonNull())
      && !jsonObj.get("state").isJsonPrimitive()) {
      throw new IllegalArgumentException(String.format(
        "Expected the field `state` to be a primitive type in the JSON string but got `%s`",
        jsonObj.get("state").toString()));
    }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      if (!Artifact.class.isAssignableFrom(type.getRawType())) {
        return null; // this class only serializes 'Artifact' and its subtypes
      }
      final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
      final TypeAdapter<Artifact> thisAdapter =
        gson.getDelegateAdapter(this, TypeToken.get(Artifact.class));

      return (TypeAdapter<T>) new TypeAdapter<Artifact>() {
        @Override
        public void write(JsonWriter out, Artifact value) throws IOException {
          JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
          elementAdapter.write(out, obj);
        }

        @Override
        public Artifact read(JsonReader in) throws IOException {
          JsonObject jsonObj = elementAdapter.read(in).getAsJsonObject();
          validateJsonObject(jsonObj);
          return thisAdapter.fromJsonTree(jsonObj);
        }
      }.nullSafe();
    }
  }

  /**
   * Create an instance of Artifact given an JSON string
   *
   * @param jsonString JSON string
   * @return An instance of Artifact
   * @throws IOException if the JSON string is invalid with respect to Artifact
   */
  public static Artifact fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, Artifact.class);
  }

  /**
   * Convert an instance of Artifact to an JSON string
   *
   * @return JSON string
   */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}
