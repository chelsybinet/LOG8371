/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.watcher.transport.actions.execute;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.xpack.core.watcher.support.xcontent.XContentSource;

import java.io.IOException;
import java.util.Objects;

/**
 * This class contains the WatchHistory generated by running the watch
 */
public class ExecuteWatchResponse extends ActionResponse implements ToXContentObject {

    public static final ParseField ID_FIELD = new ParseField("_id");
    public static final ParseField WATCH_FIELD = new ParseField("watch_record");

    private String recordId;
    private XContentSource recordSource;

    public ExecuteWatchResponse(StreamInput in) throws IOException {
        super(in);
        recordId = in.readString();
        recordSource = XContentSource.readFrom(in);
    }

    public ExecuteWatchResponse(String recordId, BytesReference recordSource, XContentType contentType) {
        this.recordId = recordId;
        this.recordSource = new XContentSource(recordSource, contentType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecuteWatchResponse that = (ExecuteWatchResponse) o;
        return Objects.equals(recordId, that.recordId) &&
            Objects.equals(recordSource, that.recordSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId, recordSource);
    }

    @Override
    public String toString() {
        return recordId + ":" + recordSource;
    }

    /**
     * @return The id of the watch record holding the watch execution result.
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @return The watch record source
     */
    public XContentSource getRecordSource() {
        return recordSource;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(recordId);
        XContentSource.writeTo(recordSource, out);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("_id", recordId);
        builder.field("watch_record");
        recordSource.toXContent(builder, params);
        builder.endObject();
        return builder;
    }

    private static final ConstructingObjectParser<ExecuteWatchResponse, Void> PARSER
        = new ConstructingObjectParser<>("x_pack_execute_watch_response", false,
        (fields) -> new ExecuteWatchResponse((String)fields[0], (BytesReference) fields[1], XContentType.JSON));
    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), ID_FIELD);
        PARSER.declareObject(ConstructingObjectParser.constructorArg(), (p, c) -> readBytesReference(p), WATCH_FIELD);
    }

    public static ExecuteWatchResponse fromXContent(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    private static BytesReference readBytesReference(XContentParser parser) throws IOException {
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.copyCurrentStructure(parser);
            return BytesReference.bytes(builder);
        }
    }
}
