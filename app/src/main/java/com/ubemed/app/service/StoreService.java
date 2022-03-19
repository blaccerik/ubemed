package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBBid;
import com.ubemed.app.dbmodel.DBPost;
import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreCats;
import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBVote;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.Post;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.BidRepository;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.apache.logging.log4j.util.PropertySource;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    @Autowired
    private BidRepository bidRepository;

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        for (DBProduct dbProduct : productRepository.findAll()) {
            if (dbProduct.isOnSale()) {
                list.add(new Product(dbProduct));
            }
        }
        return list;
    }

    public List<BidResponse> getBids(long id) {
        Optional<DBProduct> optionalDBProduct = productRepository.findById(id);
        if (optionalDBProduct.isEmpty() || !optionalDBProduct.get().isOnSale()) {
            return Collections.emptyList();
        }
        DBProduct dbProduct = optionalDBProduct.get();
        return dbProduct.getBids().stream().map(dbBid -> new BidResponse(dbBid.getDbUser().getName(), dbBid.getAmount())).collect(Collectors.toList());
    }

    private BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public boolean makeBid(String username, long id, long amount) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return false;
        }
        DBUser dbUser = optionalDBUser.get();
        Optional<DBProduct> optionalDBProduct = productRepository.findById(id);
        if (optionalDBProduct.isEmpty() || !optionalDBProduct.get().isOnSale()) {
            return false;
        }
        DBProduct dbProduct = optionalDBProduct.get();
        if (amount <= dbProduct.getCost() || dbProduct.getDbUser().getId() == dbUser.getId()) {
            return false;
        }

        // find if has already bid on it
        Optional<DBBid> optionalDBBid = dbProduct.getBids().stream().filter(
                p -> p.getDbUser().getId() == dbUser.getId()).findFirst();

        DBBid dbBid;
        long more;
        if (optionalDBBid.isPresent()) {
            dbBid = optionalDBBid.get();
            long total = dbBid.getAmount() + dbUser.getCoins();
            if (amount > total || amount <= dbBid.getAmount()) {
                return false;
            }
            more = amount - dbBid.getAmount();
            dbUser.setCoins(total - amount);
        } else {
            more = amount;
            dbUser.setCoins(dbUser.getCoins() - amount);

        }
        dbBid = new DBBid();
        dbBid.setAmount(amount);
        dbBid.setDbUser(dbUser);
        dbBid.setAmountMore(more);
        dbProduct.getBids().add(dbBid);
        dbProduct.setCost(dbBid.getAmount());
        productRepository.save(dbProduct);
        userRepository.save(dbUser);
        return true;
    }

    @Transactional
    public void endBids() {

        Date date = new Date();
        List<DBProduct> list = productRepository.findAll();
        for (DBProduct dbProduct : list) {
            if (dbProduct.isOnSale()) {
                long secs = (date.getTime() -  dbProduct.getDate().getTime()) / 1000;
                long hours = secs / 3600;
                if (hours >= 24 || hours == 0) {
                    // get top bid
                    Optional<DBBid> optionalDBBid = dbProduct.getBids().stream().max(Comparator.comparingLong(DBBid::getAmount));
                    DBUser oldUser = dbProduct.getDbUser();

                    if (optionalDBBid.isPresent()) {

                        DBBid topBid = optionalDBBid.get();
                        DBUser newUser = topBid.getDbUser();

                        // refund bids
                        for (DBBid dbBid : dbProduct.getBids()) {
                            DBUser dbUser = dbBid.getDbUser();
                            if (dbUser.getId() != newUser.getId()) {
                                dbUser.setCoins(dbUser.getCoins() + dbBid.getAmountMore());
                                userRepository.save(dbUser);
                            }
                        }
                        dbProduct.setDbUser(newUser);
                        dbProduct.setOnSale(false);

                        oldUser.setCoins(oldUser.getCoins() + (topBid.getAmount() / 2) + dbProduct.getStartPrice());
                        oldUser.getProducts().remove(dbProduct);

                        newUser.getProducts().add(dbProduct);

                        userRepository.save(oldUser);
                        userRepository.save(newUser);

//                        bidRepository.
                    }
                    else  {
                        oldUser.setCoins(oldUser.getCoins() + (dbProduct.getCost() / 2));
                        oldUser.getProducts().remove(dbProduct);
                        userRepository.save(oldUser);
                    }
                }
            }
        }
    }

    public boolean save(String username, String title, List<Long> cats, long cost, MultipartFile file) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        DBUser dbUser;
        if (optionalDBUser.isEmpty() || optionalDBUser.get().getCoins() < cost) {
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
            dbProduct.setDate(new Date());
            dbProduct.setOnSale(true);
            dbProduct.setBids(new ArrayList<>());
            dbProduct.setStartPrice(cost);

            dbUser.getProducts().add(dbProduct);
            dbUser.setCoins(dbUser.getCoins() - cost);
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
}
