package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreCats;
import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@org.springframework.stereotype.Service
public class StoreService {

    private final int height = 200;
    private final int width = 200;
    private final String imageName = "image";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CatRepository catRepository;

    public List<Product> getAll() {
        return productRepository.findAll().stream().map(dbProduct -> new Product(dbProduct)).collect(Collectors.toList());
    }

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public boolean save(String username, String title, List<Long> cats, long cost, MultipartFile file) {

        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        DBUser dbUser;
        if (optionalDBUser.isEmpty()) {
            return false;
        }
        dbUser = optionalDBUser.get();
        if (file.getContentType() == null || !file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpeg")) {
            return false;
        }
        try {
            List<DBStoreCats> catsList = new ArrayList<>();
            for (Long aLong : cats) {
                Optional<DBStoreCats> optionalDBStoreCats = catRepository.findByCode(aLong);
                if (optionalDBStoreCats.isEmpty()) {
                    return false;
                }
                catsList.add(optionalDBStoreCats.get());
            }


            System.out.println(file.getBytes().length);
            DBStoreImage dbStoreImage = modifyFile(file);
            System.out.println(dbStoreImage.getFile().length);

            DBProduct dbProduct = new DBProduct();
            dbProduct.setDbUser(dbUser);
            dbProduct.setDbStoreImage(dbStoreImage);
            dbStoreImage.setDbProduct(dbProduct);

            dbProduct.setCost(cost);
            dbProduct.setTitle(title);
            dbProduct.setDbStoreCats(catsList);

            dbUser.getProducts().add(dbProduct);
            userRepository.save(dbUser);
            return true;
        } catch (IOException ioException) {
            return false;
        }
    }

    private DBStoreImage modifyFile(MultipartFile file) throws IOException {

        // translate to bufferedimage
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();

        // resize
        bufferedImage = resizeImage(bufferedImage);

        // back to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        // compress
        return new DBStoreImage(imageName, file.getContentType(), compressBytes(bytes));
    }


    // compress the image bytes before storing it in the database
    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ignored) {}
        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ignored) {}
        return outputStream.toByteArray();
    }

    public Product get(long id) {
        Optional<DBProduct> optionalDBProduct = productRepository.findById(id);
        if (optionalDBProduct.isPresent()) {
            DBProduct dbProduct = optionalDBProduct.get();
            return new Product(dbProduct);
        }
        return null;
    }
}
