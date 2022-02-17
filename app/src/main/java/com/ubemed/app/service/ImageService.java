package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.model.ProductImage;
import com.ubemed.app.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.ubemed.app.service.StoreService.decompressBytes;

@org.springframework.stereotype.Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public ProductImage get(long id) {
        Optional<DBStoreImage> optionalDBStoreImage = imageRepository.findById(id);
        if (optionalDBStoreImage.isEmpty()) {
            return null;
        }
        DBStoreImage dbStoreImage = optionalDBStoreImage.get();
        return new ProductImage(decompressBytes(dbStoreImage.getFile()));
    }
}
