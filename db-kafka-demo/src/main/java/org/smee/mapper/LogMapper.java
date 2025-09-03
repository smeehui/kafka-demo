package org.smee.mapper;

import org.bson.Document;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.smee.dto.Logs;

@Mapper
public interface LogMapper {
    LogMapper INSTANCE = Mappers.getMapper(LogMapper.class);

    default Document toMongoDocument(Logs log) {
        Document doc = new Document();
        doc.append("log", log.getLog());
        doc.append("time", log.getTime());
        doc.append("before", log.getBefore());
        doc.append("after", log.getAfter());
        doc.append("log_type", log.getLogType());
        return doc;
    }
}
