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


package org.lockss.laaws.api.rs;

import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lockss.laaws.client.ApiCallback;
import org.lockss.laaws.client.ApiException;
import org.lockss.laaws.client.ApiResponse;
import org.lockss.laaws.client.Configuration;
import org.lockss.laaws.client.Pair;
import org.lockss.laaws.client.V2RestClient;

public class WaybackApi {

  private V2RestClient apiClient;

  public WaybackApi() {
    this(Configuration.getDefaultApiClient());
  }

  public WaybackApi(V2RestClient apiClient) {
    this.apiClient = apiClient;
  }

  public V2RestClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(V2RestClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Build call for getCdxOwb
   *
   * @param collectionid   Identifier of the collection (required)
   * @param q              Query string. Supported fields are url, type\\ \\ (urlquery/prefixquery), offset and limit. (optional)
   * @param count          . (optional)
   * @param startPage      . (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param _callback      Callback for upload/download progress
   * @return Call to execute
   * @throws ApiException If fail to serialize the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The OpenWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getCdxOwbCall(String collectionid, String q, Integer count, Integer startPage,
    String accept, String acceptEncoding, final ApiCallback _callback) throws ApiException {
    Object localVarPostBody = null;

    // create path and map variables
    String localVarPath = "/cdx/owb/{collectionid}"
      .replaceAll("\\{" + "collectionid" + "\\}", apiClient.escapeString(collectionid));

    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    if (q != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("q", q));
    }

    if (count != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("count", count));
    }

    if (startPage != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("start_page", startPage));
    }

    if (accept != null) {
      localVarHeaderParams.put("Accept", apiClient.parameterToString(accept));
    }

    if (acceptEncoding != null) {
      localVarHeaderParams.put("Accept-Encoding", apiClient.parameterToString(acceptEncoding));
    }

    final String[] localVarAccepts = {
      "application/xml"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    if (localVarAccept != null) {
      localVarHeaderParams.put("Accept", localVarAccept);
    }

    final String[] localVarContentTypes = {

    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    localVarHeaderParams.put("Content-Type", localVarContentType);

    String[] localVarAuthNames = new String[]{};
    return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
      localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams,
      localVarFormParams, localVarAuthNames, _callback);
  }

  @SuppressWarnings("rawtypes")
  private okhttp3.Call getCdxOwbValidateBeforeCall(String collectionid, String q, Integer count,
    Integer startPage, String accept, String acceptEncoding, final ApiCallback _callback)
    throws ApiException {

    // verify the required parameter 'collectionid' is set
    if (collectionid == null) {
      throw new ApiException(
        "Missing the required parameter 'collectionid' when calling getCdxOwb(Async)");
    }

    okhttp3.Call localVarCall = getCdxOwbCall(collectionid, q, count, startPage, accept,
      acceptEncoding, _callback);
    return localVarCall;

  }

  /**
   * Get OpenWayback CDX records
   * Get the OpenWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param q              Query string. Supported fields are url, type\\ \\ (urlquery/prefixquery), offset and limit. (optional)
   * @param count          . (optional)
   * @param startPage      . (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @return String
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The OpenWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public String getCdxOwb(String collectionid, String q, Integer count, Integer startPage,
    String accept, String acceptEncoding) throws ApiException {
    ApiResponse<String> localVarResp = getCdxOwbWithHttpInfo(collectionid, q, count, startPage,
      accept, acceptEncoding);
    return localVarResp.getData();
  }

  /**
   * Get OpenWayback CDX records
   * Get the OpenWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param q              Query string. Supported fields are url, type\\ \\ (urlquery/prefixquery), offset and limit. (optional)
   * @param count          . (optional)
   * @param startPage      . (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The OpenWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public ApiResponse<String> getCdxOwbWithHttpInfo(String collectionid, String q, Integer count,
    Integer startPage, String accept, String acceptEncoding) throws ApiException {
    okhttp3.Call localVarCall = getCdxOwbValidateBeforeCall(collectionid, q, count, startPage,
      accept, acceptEncoding, null);
    Type localVarReturnType = new TypeToken<String>() {
    }.getType();
    return apiClient.execute(localVarCall, localVarReturnType);
  }

  /**
   * Get OpenWayback CDX records (asynchronously)
   * Get the OpenWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param q              Query string. Supported fields are url, type\\ \\ (urlquery/prefixquery), offset and limit. (optional)
   * @param count          . (optional)
   * @param startPage      . (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param _callback      The callback to be executed when the API call finishes
   * @return The request call
   * @throws ApiException If fail to process the API call, e.g. serializing the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The OpenWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getCdxOwbAsync(String collectionid, String q, Integer count,
    Integer startPage, String accept, String acceptEncoding, final ApiCallback<String> _callback)
    throws ApiException {

    okhttp3.Call localVarCall = getCdxOwbValidateBeforeCall(collectionid, q, count, startPage,
      accept, acceptEncoding, _callback);
    Type localVarReturnType = new TypeToken<String>() {
    }.getType();
    apiClient.executeAsync(localVarCall, localVarReturnType, _callback);
    return localVarCall;
  }

  /**
   * Build call for getCdxPywb
   *
   * @param collectionid   Identifier of the collection (required)
   * @param url            The URL for which the CDX records are requested (optional)
   * @param limit          . (optional)
   * @param matchType      (optional)
   * @param sort           (optional)
   * @param closest        Timestamp for sort&#x3D;closest mode (optional)
   * @param output         Output format (optional)
   * @param fl             Comma-separated list of fields to include in output (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param _callback      Callback for upload/download progress
   * @return Call to execute
   * @throws ApiException If fail to serialize the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The PyWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getCdxPywbCall(String collectionid, String url, Integer limit,
    String matchType, String sort, String closest, String output, String fl, String accept,
    String acceptEncoding, final ApiCallback _callback) throws ApiException {
    Object localVarPostBody = null;

    // create path and map variables
    String localVarPath = "/cdx/pywb/{collectionid}"
      .replaceAll("\\{" + "collectionid" + "\\}", apiClient.escapeString(collectionid));

    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    if (url != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("url", url));
    }

    if (limit != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("limit", limit));
    }

    if (matchType != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("matchType", matchType));
    }

    if (sort != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("sort", sort));
    }

    if (closest != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("closest", closest));
    }

    if (output != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("output", output));
    }

    if (fl != null) {
      localVarQueryParams.addAll(apiClient.parameterToPair("fl", fl));
    }

    if (accept != null) {
      localVarHeaderParams.put("Accept", apiClient.parameterToString(accept));
    }

    if (acceptEncoding != null) {
      localVarHeaderParams.put("Accept-Encoding", apiClient.parameterToString(acceptEncoding));
    }

    final String[] localVarAccepts = {
      "text/plain"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    if (localVarAccept != null) {
      localVarHeaderParams.put("Accept", localVarAccept);
    }

    final String[] localVarContentTypes = {

    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    localVarHeaderParams.put("Content-Type", localVarContentType);

    String[] localVarAuthNames = new String[]{};
    return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
      localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams,
      localVarFormParams, localVarAuthNames, _callback);
  }

  @SuppressWarnings("rawtypes")
  private okhttp3.Call getCdxPywbValidateBeforeCall(String collectionid, String url, Integer limit,
    String matchType, String sort, String closest, String output, String fl, String accept,
    String acceptEncoding, final ApiCallback _callback) throws ApiException {

    // verify the required parameter 'collectionid' is set
    if (collectionid == null) {
      throw new ApiException(
        "Missing the required parameter 'collectionid' when calling getCdxPywb(Async)");
    }

    okhttp3.Call localVarCall = getCdxPywbCall(collectionid, url, limit, matchType, sort, closest,
      output, fl, accept, acceptEncoding, _callback);
    return localVarCall;

  }

  /**
   * Get PyWayback CDX records
   * Get the PyWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param url            The URL for which the CDX records are requested (optional)
   * @param limit          . (optional)
   * @param matchType      (optional)
   * @param sort           (optional)
   * @param closest        Timestamp for sort&#x3D;closest mode (optional)
   * @param output         Output format (optional)
   * @param fl             Comma-separated list of fields to include in output (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @return String
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The PyWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public String getCdxPywb(String collectionid, String url, Integer limit, String matchType,
    String sort, String closest, String output, String fl, String accept, String acceptEncoding)
    throws ApiException {
    ApiResponse<String> localVarResp = getCdxPywbWithHttpInfo(collectionid, url, limit, matchType,
      sort, closest, output, fl, accept, acceptEncoding);
    return localVarResp.getData();
  }

  /**
   * Get PyWayback CDX records
   * Get the PyWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param url            The URL for which the CDX records are requested (optional)
   * @param limit          . (optional)
   * @param matchType      (optional)
   * @param sort           (optional)
   * @param closest        Timestamp for sort&#x3D;closest mode (optional)
   * @param output         Output format (optional)
   * @param fl             Comma-separated list of fields to include in output (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @return ApiResponse&lt;String&gt;
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The PyWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public ApiResponse<String> getCdxPywbWithHttpInfo(String collectionid, String url, Integer limit,
    String matchType, String sort, String closest, String output, String fl, String accept,
    String acceptEncoding) throws ApiException {
    okhttp3.Call localVarCall = getCdxPywbValidateBeforeCall(collectionid, url, limit, matchType,
      sort, closest, output, fl, accept, acceptEncoding, null);
    Type localVarReturnType = new TypeToken<String>() {
    }.getType();
    return apiClient.execute(localVarCall, localVarReturnType);
  }

  /**
   * Get PyWayback CDX records (asynchronously)
   * Get the PyWayback CDX records of a URL in a collection
   *
   * @param collectionid   Identifier of the collection (required)
   * @param url            The URL for which the CDX records are requested (optional)
   * @param limit          . (optional)
   * @param matchType      (optional)
   * @param sort           (optional)
   * @param closest        Timestamp for sort&#x3D;closest mode (optional)
   * @param output         Output format (optional)
   * @param fl             Comma-separated list of fields to include in output (optional)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param _callback      The callback to be executed when the API call finishes
   * @return The request call
   * @throws ApiException If fail to process the API call, e.g. serializing the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The PyWayback CDX records of the URL in the collection </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 403 </td><td> Forbidden </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> Collection/URL not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getCdxPywbAsync(String collectionid, String url, Integer limit,
    String matchType, String sort, String closest, String output, String fl, String accept,
    String acceptEncoding, final ApiCallback<String> _callback) throws ApiException {

    okhttp3.Call localVarCall = getCdxPywbValidateBeforeCall(collectionid, url, limit, matchType,
      sort, closest, output, fl, accept, acceptEncoding, _callback);
    Type localVarReturnType = new TypeToken<String>() {
    }.getType();
    apiClient.executeAsync(localVarCall, localVarReturnType, _callback);
    return localVarCall;
  }

  /**
   * Build call for getWarcArchive
   *
   * @param fileName       Name of the WARC archive (required)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param range          The Range header (optional)
   * @param _callback      Callback for upload/download progress
   * @return Call to execute
   * @throws ApiException If fail to serialize the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The contents of the requested WARC archive </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> WARC archive not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getWarcArchiveCall(String fileName, String accept, String acceptEncoding,
    String range, final ApiCallback _callback) throws ApiException {
    Object localVarPostBody = null;

    // create path and map variables
    String localVarPath = "/warcs/{fileName}"
      .replaceAll("\\{" + "fileName" + "\\}", apiClient.escapeString(fileName));

    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();

    if (accept != null) {
      localVarHeaderParams.put("Accept", apiClient.parameterToString(accept));
    }

    if (acceptEncoding != null) {
      localVarHeaderParams.put("Accept-Encoding", apiClient.parameterToString(acceptEncoding));
    }

    if (range != null) {
      localVarHeaderParams.put("Range", apiClient.parameterToString(range));
    }

    final String[] localVarAccepts = {
      "application/warc"
    };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    if (localVarAccept != null) {
      localVarHeaderParams.put("Accept", localVarAccept);
    }

    final String[] localVarContentTypes = {

    };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    localVarHeaderParams.put("Content-Type", localVarContentType);

    String[] localVarAuthNames = new String[]{};
    return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
      localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams,
      localVarFormParams, localVarAuthNames, _callback);
  }

  @SuppressWarnings("rawtypes")
  private okhttp3.Call getWarcArchiveValidateBeforeCall(String fileName, String accept,
    String acceptEncoding, String range, final ApiCallback _callback) throws ApiException {

    // verify the required parameter 'fileName' is set
    if (fileName == null) {
      throw new ApiException(
        "Missing the required parameter 'fileName' when calling getWarcArchive(Async)");
    }

    okhttp3.Call localVarCall = getWarcArchiveCall(fileName, accept, acceptEncoding, range,
      _callback);
    return localVarCall;

  }

  /**
   * Get a WARC archive
   * Get the contents of a single WARC record as a WARC archive
   *
   * @param fileName       Name of the WARC archive (required)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param range          The Range header (optional)
   * @return File
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The contents of the requested WARC archive </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> WARC archive not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public File getWarcArchive(String fileName, String accept, String acceptEncoding, String range)
    throws ApiException {
    ApiResponse<File> localVarResp = getWarcArchiveWithHttpInfo(fileName, accept, acceptEncoding,
      range);
    return localVarResp.getData();
  }

  /**
   * Get a WARC archive
   * Get the contents of a single WARC record as a WARC archive
   *
   * @param fileName       Name of the WARC archive (required)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param range          The Range header (optional)
   * @return ApiResponse&lt;File&gt;
   * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The contents of the requested WARC archive </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> WARC archive not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public ApiResponse<File> getWarcArchiveWithHttpInfo(String fileName, String accept,
    String acceptEncoding, String range) throws ApiException {
    okhttp3.Call localVarCall = getWarcArchiveValidateBeforeCall(fileName, accept, acceptEncoding,
      range, null);
    Type localVarReturnType = new TypeToken<File>() {
    }.getType();
    return apiClient.execute(localVarCall, localVarReturnType);
  }

  /**
   * Get a WARC archive (asynchronously)
   * Get the contents of a single WARC record as a WARC archive
   *
   * @param fileName       Name of the WARC archive (required)
   * @param accept         The Accept header (optional)
   * @param acceptEncoding The Accept-Encoding header (optional)
   * @param range          The Range header (optional)
   * @param _callback      The callback to be executed when the API call finishes
   * @return The request call
   * @throws ApiException If fail to process the API call, e.g. serializing the request body object
   * @http.response.details <table summary="Response Details" border="1">
   * <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
   * <tr><td> 200 </td><td> The contents of the requested WARC archive </td><td>  -  </td></tr>
   * <tr><td> 401 </td><td> Unauthorized </td><td>  -  </td></tr>
   * <tr><td> 404 </td><td> WARC archive not found </td><td>  -  </td></tr>
   * <tr><td> 500 </td><td> Internal Server Error </td><td>  -  </td></tr>
   * </table>
   */
  public okhttp3.Call getWarcArchiveAsync(String fileName, String accept, String acceptEncoding,
    String range, final ApiCallback<File> _callback) throws ApiException {

    okhttp3.Call localVarCall = getWarcArchiveValidateBeforeCall(fileName, accept, acceptEncoding,
      range, _callback);
    Type localVarReturnType = new TypeToken<File>() {
    }.getType();
    apiClient.executeAsync(localVarCall, localVarReturnType, _callback);
    return localVarCall;
  }
}
