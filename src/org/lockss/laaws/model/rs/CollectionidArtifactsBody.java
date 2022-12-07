/*
 * 2022, Board of Trustees of Leland Stanford Jr. University,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * LOCKSS Repository Service REST API
 * REST API of the LOCKSS Repository Service
 *
 * OpenAPI spec version: 2.0.0
 * Contact: lockss-support@lockss.org
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package org.lockss.laaws.model.rs;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.Objects;
/**
 * CollectionidArtifactsBody
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-22T11:18:26.304-07:00[America/Los_Angeles]")
public class CollectionidArtifactsBody {
  @SerializedName("auid")
  private String auid = null;

  @SerializedName("uri")
  private String uri = null;

  @SerializedName("collectionDate")
  private Long collectionDate = null;

  @SerializedName("artifact")
  private File artifact = null;

  public CollectionidArtifactsBody auid(String auid) {
    this.auid = auid;
    return this;
  }

   /**
   * Archival Unit ID (AUID) of new artifact
   * @return auid
   **/
  public String getAuid() {
    return auid;
  }

  public void setAuid(String auid) {
    this.auid = auid;
  }

  public CollectionidArtifactsBody uri(String uri) {
    this.uri = uri;
    return this;
  }

   /**
   * URI represented by this artifact
   * @return uri
   **/
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public CollectionidArtifactsBody collectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
    return this;
  }

   /**
   * Artifact collection/crawl date (milliseconds since epoch; UTC)
   * @return collectionDate
   **/
  public Long getCollectionDate() {
    return collectionDate;
  }

  public void setCollectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
  }

  public CollectionidArtifactsBody artifact(File artifact) {
    this.artifact = artifact;
    return this;
  }

   /**
   * Artifact data
   * @return artifact
   **/
  public File getArtifact() {
    return artifact;
  }

  public void setArtifact(File artifact) {
    this.artifact = artifact;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionidArtifactsBody collectionidArtifactsBody = (CollectionidArtifactsBody) o;
    return Objects.equals(this.auid, collectionidArtifactsBody.auid) &&
        Objects.equals(this.uri, collectionidArtifactsBody.uri) &&
        Objects.equals(this.collectionDate, collectionidArtifactsBody.collectionDate) &&
        Objects.equals(this.artifact, collectionidArtifactsBody.artifact);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auid, uri, collectionDate, Objects.hashCode(artifact));
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CollectionidArtifactsBody {\n");
    
    sb.append("    auid: ").append(toIndentedString(auid)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    collectionDate: ").append(toIndentedString(collectionDate)).append("\n");
    sb.append("    artifact: ").append(toIndentedString(artifact)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
