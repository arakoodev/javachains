package com.edgechain.lib.index.client.impl;

import com.edgechain.lib.embeddings.WordEmbeddings;
import com.edgechain.lib.endpoint.impl.PostgresEndpoint;
import com.edgechain.lib.index.domain.PostgresWordEmbeddings;
import com.edgechain.lib.index.enums.PostgresDistanceMetric;
import com.edgechain.lib.response.StringResponse;
import com.edgechain.lib.rxjava.transformer.observable.EdgeChain;
import com.edgechain.testutil.PostgresTestContainer;
import com.edgechain.testutil.PostgresTestContainer.DB;
import com.zaxxer.hikari.HikariConfig;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PostgresClientTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresClientTest.class);

  private static final float FLOAT_ERROR_MARGIN = 0.0001f;

  private static PostgresTestContainer instance = new PostgresTestContainer(DB.VECTOR);

  @BeforeAll
  static void setupAll() {
    instance.start();
  }

  @AfterAll
  static void teardownAll() {
    instance.stop();
  }

  @Autowired
  private HikariConfig hikariConfig;

  @Autowired
  private PostgresClient service;

  @BeforeEach
  void setupEach() {
    // hikari has own copy of properties so set these here
    hikariConfig.setJdbcUrl(instance.getJdbcUrl());
    hikariConfig.setUsername(instance.getUsername());
    hikariConfig.setPassword(instance.getPassword());
  }

  @Test
  void allMethods() {
    createTable();
    createMetadataTable();
    deleteAll(); // check delete before we get foreign keys

    String uuid1 = upsert();
    batchUpsert();
    query_noMeta();

    String uuid2 = insertMetadata();
    batchInsertMetadata();
    insertIntoJoinTable(uuid1, uuid2);
    query_meta();

    getChunks();
    getSimilarChunks();
  }

  private void createTable() {
    createTable_metric(PostgresDistanceMetric.L2, "testtableL2");
    createTable_metric(PostgresDistanceMetric.COSINE, "testtableCOS");
    createTable_metric(null, "testtable");

    // create table again
    createTable_metric(null, "testtable");
}
  
  private void createTable_metric(PostgresDistanceMetric metric, String tableName) {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn(tableName);
    when(mockPe.getLists()).thenReturn(1);
    when(mockPe.getDimensions()).thenReturn(2);
    when(mockPe.getMetric()).thenReturn(metric);

    final Data data = new Data();
    EdgeChain<StringResponse> result = service.createTable(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("createTable failed", data.error);
    }
    LOGGER.info("createTable (metric={}) response: '{}'", metric, data.val);
  }

  private void createMetadataTable() {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));

    final Data data = new Data();
    EdgeChain<StringResponse> result = service.createMetadataTable(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("createMetadataTable failed", data.error);
    }
    LOGGER.info("createMetadataTable response: '{}'", data.val);
  }

  private String upsert() {
    WordEmbeddings we = new WordEmbeddings();
    we.setId("WE1");
    we.setScore("101");
    we.setValues(List.of(0.25f, 0.5f));

    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getWordEmbedding()).thenReturn(we);
    when(mockPe.getFilename()).thenReturn("readme.pdf");
    when(mockPe.getNamespace()).thenReturn("testns");

    final Data data = new Data();
    EdgeChain<StringResponse> result = service.upsert(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("upsert failed", data.error);
    }
    LOGGER.info("upsert response: '{}'", data.val);
    return data.val;
  }

  private String insertMetadata() {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));
    when(mockPe.getMetadata()).thenReturn("''duck''");
    when(mockPe.getDocumentDate()).thenReturn("November 11, 2015");

    final Data data = new Data();
    EdgeChain<StringResponse> result = service.insertMetadata(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("insertMetadata failed", data.error);
    }
    LOGGER.info("insertMetadata response: '{}'", data.val);
    return data.val;
  }

  private void batchUpsert() {
    WordEmbeddings we1 = new WordEmbeddings();
    we1.setId("WE1");
    we1.setScore("101");
    we1.setValues(List.of(0.25f, 0.5f));

    WordEmbeddings we2 = new WordEmbeddings();
    we2.setId("WE2");
    we2.setScore("202");
    we2.setValues(List.of(0.75f, 0.9f));

    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getWordEmbeddingsList()).thenReturn(List.of(we1, we2));
    when(mockPe.getFilename()).thenReturn("readme.pdf");
    when(mockPe.getNamespace()).thenReturn("testns");

    final Data data = new Data();
    EdgeChain<List<StringResponse>> result = service.batchUpsert(mockPe);
    result.toSingle().blockingSubscribe(
        s -> data.val = s.stream().map(r -> r.getResponse()).collect(Collectors.joining(",")),
        e -> data.error = e);
    if (data.error != null) {
      fail("batchUpsert failed", data.error);
    }
    LOGGER.info("batchUpsert response: '{}'", data.val);
  }

  private void batchInsertMetadata() {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));
    when(mockPe.getMetadataList()).thenReturn(List.of("cow", "horse"));

    final Data data = new Data();
    EdgeChain<List<StringResponse>> result = service.batchInsertMetadata(mockPe);
    result.toSingle().blockingSubscribe(
        s -> data.val = s.stream().map(r -> r.getResponse()).collect(Collectors.joining(",")),
        e -> data.error = e);
    if (data.error != null) {
      fail("batchInsertMetadata failed", data.error);
    }
    LOGGER.info("batchInsertMetadata response: '{}'", data.val);
  }

  private void insertIntoJoinTable(String uuid1, String uuid2) {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));
    when(mockPe.getId()).thenReturn(uuid1);
    when(mockPe.getMetadataId()).thenReturn(uuid2);

    final Data data = new Data();

    EdgeChain<StringResponse> result = service.insertIntoJoinTable(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("insertIntoJoinTable failed", data.error);
    }
    LOGGER.info("insertIntoJoinTable response: '{}'", data.val);
  }

  private void deleteAll() {
    deleteAll_namespace(null, "knowledge");
    deleteAll_namespace("", "knowledge");
    deleteAll_namespace("testns", "testns");
  }
  
  private void deleteAll_namespace(String namespace, String expected) {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getNamespace()).thenReturn(namespace);

    final Data data = new Data();
    EdgeChain<StringResponse> result = service.deleteAll(mockPe);
    result.toSingle().blockingSubscribe(s -> data.val = s.getResponse(), e -> data.error = e);
    if (data.error != null) {
      fail("deleteAll failed", data.error);
    }
    LOGGER.info("deleteAll (namespace={}) response: '{}'", namespace, data.val);
    assertTrue(data.val.endsWith(expected));
  }

  private void query_noMeta() {
    query_noMeta_metric(PostgresDistanceMetric.COSINE);
    query_noMeta_metric(PostgresDistanceMetric.IP);
    query_noMeta_metric(PostgresDistanceMetric.L2);
  }
  
  private void query_noMeta_metric(PostgresDistanceMetric metric) {
    WordEmbeddings we1 = new WordEmbeddings();
    we1.setId("WEQUERY");
    we1.setScore("104");
    we1.setValues(List.of(0.25f, 0.5f));

    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getNamespace()).thenReturn("testns");
    when(mockPe.getProbes()).thenReturn(5);
    when(mockPe.getMetric()).thenReturn(metric);
    when(mockPe.getWordEmbedding()).thenReturn(we1);
    when(mockPe.getTopK()).thenReturn(1000);
    when(mockPe.getMetadataTableNames()).thenReturn(null);

    final Data data = new Data();
    EdgeChain<List<PostgresWordEmbeddings>> result = service.query(mockPe);
    result.toSingle().blockingSubscribe(
        s -> data.val = s.stream().map(r -> r.getRawText()).collect(Collectors.joining(",")),
        e -> data.error = e);
    if (data.error != null) {
      fail("query (no meta) failed", data.error);
    }
    LOGGER.info("query no meta (metric={}) response: '{}'", metric, data.val);

    // WE1 from single upsert, and WE2 from batch upsert
    assertTrue(data.val.contains("WE1") && data.val.contains("WE2"));
  }

  private void query_meta() {
    query_meta_metric(PostgresDistanceMetric.COSINE);
    query_meta_metric(PostgresDistanceMetric.IP);
    query_meta_metric(PostgresDistanceMetric.L2);
  }
  
  private void query_meta_metric(PostgresDistanceMetric metric) {
    WordEmbeddings we1 = new WordEmbeddings();
    we1.setId("WEQUERY");
    we1.setScore("104");
    we1.setValues(List.of(0.25f, 0.5f));

    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getNamespace()).thenReturn("testns");
    when(mockPe.getProbes()).thenReturn(5);
    when(mockPe.getMetric()).thenReturn(metric);
    when(mockPe.getWordEmbedding()).thenReturn(we1);
    when(mockPe.getTopK()).thenReturn(1000);
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));

    final Data data = new Data();
    EdgeChain<List<PostgresWordEmbeddings>> result = service.query(mockPe);
    result.toSingle().blockingSubscribe(
        s -> data.val = s.stream().map(r -> r.getRawText()).collect(Collectors.joining(",")),
        e -> data.error = e);
    if (data.error != null) {
      fail("query (meta) failed", data.error);
    }
    LOGGER.info("query with meta (metric={}) response: '{}'", metric, data.val);

    // WE1 from single joined upsert
    assertTrue(data.val.contains("WE1"));
  }

  private void getChunks() {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getTableName()).thenReturn("testtable");
    when(mockPe.getFilename()).thenReturn("readme.pdf");

    final Data data = new Data();
    EdgeChain<List<PostgresWordEmbeddings>> result = service.getAllChunks(mockPe);
    result.toSingle().blockingSubscribe(s -> {
      data.list = s;
      data.val = s.stream().map(r -> r.getRawText()).collect(Collectors.joining(","));
    }, e -> data.error = e);
    if (data.error != null) {
      fail("getChunks failed", data.error);
    }
    LOGGER.info("getChunks response: '{}'", data.val);

    // WE1 from single upsert, and WE2 from batch upsert
    assertTrue(data.val.contains("WE1") && data.val.contains("WE2"));

    PostgresWordEmbeddings first = data.list.get(0);
    assertEquals(0.25f, first.getValues().get(0), FLOAT_ERROR_MARGIN);
    assertEquals(0.5f, first.getValues().get(1), FLOAT_ERROR_MARGIN);

    PostgresWordEmbeddings second = data.list.get(1);
    assertEquals(0.75f, second.getValues().get(0), FLOAT_ERROR_MARGIN);
    assertEquals(0.9f, second.getValues().get(1), FLOAT_ERROR_MARGIN);
  }

  private void getSimilarChunks() {
    PostgresEndpoint mockPe = mock(PostgresEndpoint.class);
    when(mockPe.getMetadataTableNames()).thenReturn(List.of("dogmeta", "catmeta"));
    when(mockPe.getEmbeddingChunk()).thenReturn("how to test this");

    final Data data = new Data();
    EdgeChain<List<PostgresWordEmbeddings>> result = service.getSimilarMetadataChunk(mockPe);
    result.toSingle().blockingSubscribe(s -> {
      data.list = s;
      data.val = s.stream().map(r -> r.getRawText()).collect(Collectors.joining(","));
    }, e -> data.error = e);
    if (data.error != null) {
      fail("getSimilarMetadataChunk failed", data.error);
    }
    LOGGER.info("getSimilarMetadataChunk response: '{}'", data.val);
  }

  private static class Data {
    public Throwable error;
    public String val;
    public List<PostgresWordEmbeddings> list;
  }

}
