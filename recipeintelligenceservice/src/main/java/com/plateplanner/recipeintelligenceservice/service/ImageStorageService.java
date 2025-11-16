package com.plateplanner.recipeintelligenceservice.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFsBucket;

    public ObjectId store(MultipartFile file, String userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        BasicDBObject metadata = new BasicDBObject();
        metadata.put("userId", userId);
        metadata.put("contentType", file.getContentType());
        metadata.put("size", file.getSize());

        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata);
    }

    public GridFSFile getFile(String id) {
        GridFSFile file;
        try {
            file = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image id");
        }
        if (file == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        return file;
    }

    public void assertOwner(GridFSFile file, String userId) {
        Document md = file.getMetadata();
        String owner = Optional.ofNullable(md).map(m -> m.getString("userId")).orElse(null);
        if (owner == null || !owner.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this image");
        }
    }

    public GridFsResource getResource(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }

    public void delete(String id, String userId) {
        GridFSFile file = getFile(id);
        assertOwner(file, userId);
        gridFsBucket.delete(file.getObjectId());
    }

    public byte[] loadBytes(String id) throws IOException {
        GridFSFile file = getFile(id);
        GridFsResource resource = getResource(file);
        return resource.getInputStream().readAllBytes();
    }

    public String contentType(GridFSFile file) {
        Document md = file.getMetadata();
        if (md != null && md.getString("contentType") != null) return md.getString("contentType");
        return "application/octet-stream";
    }

    public String filename(GridFSFile file) {
        return file.getFilename() != null ? file.getFilename() : (file.getObjectId().toHexString() + ".bin");
    }

    public List<GridFSFile> findAllByUser(String userId) {
        return gridFsTemplate.find(
                Query.query(Criteria.where("metadata.userId").is(userId))
        ).into(new ArrayList<>());
    }
}

