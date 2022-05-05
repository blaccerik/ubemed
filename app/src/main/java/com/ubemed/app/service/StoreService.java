package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBBid;
import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBStoreCats;
import com.ubemed.app.dbmodel.DBStoreImage;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.Product;
import com.ubemed.app.repository.BidRepository;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.ProductStateRepository;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@org.springframework.stereotype.Service
public class StoreService {

    public enum strategy {
        normal,
        test
    }

    private static final int height = 200;
    private static final int width = 200;
    private static final String imageName = "image";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CatRepository catRepository;

    private final ProductStateRepository productStateRepository;

    @Autowired
    public StoreService(ProductRepository productRepository,
                        UserRepository userRepository,
                        CatRepository catRepository,
                        ProductStateRepository productStateRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.catRepository = catRepository;
        this.productStateRepository = productStateRepository;
    }

    public List<Product> getAll(Integer page, String filter, String search) {
        return getFiltered(page, filter, search).stream()
                .filter(o -> o.getDbProductState().getState().equals(DBProductState.states.sale))
                .map(o -> new Product(o)).collect(Collectors.toList());
    }

    private List<DBProduct> getFiltered(Integer page, String filter, String search) {

        if (search == null) {
            search = "";
        }

        if (filter == null) {
            filter = "";
        }

        switch (filter) {
            case "new":
                return productRepository.findNew(search);
            case "cheap":
                return productRepository.findCheap(search);
            case "expensive":
                return productRepository.findExpensive(search);
            case "hot":
                return productRepository.findHot(search);
        }

        return productRepository.findDefault(search);
    }

    public List<BidResponse> getBids(long id) {
        Optional<DBProduct> optionalDBProduct = productRepository.findById(id);
        if (optionalDBProduct.isEmpty() || !optionalDBProduct.get().getDbProductState().getState().equals(DBProductState.states.sale)) {
            return Collections.emptyList();
        }
        DBProduct dbProduct = optionalDBProduct.get();
        return dbProduct.getBids().stream().map(dbBid -> new BidResponse(dbBid.getDbUser().getName(), dbBid.getId(), dbBid.getAmount())).collect(Collectors.toList());
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }



    @Transactional
    public boolean makeBid(String username, long id, long amount) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return false;
        }
        DBUser dbUser = optionalDBUser.get();

        Optional<DBProduct> optionalDBProduct = productRepository.findById(id);
        if (optionalDBProduct.isEmpty() || !optionalDBProduct.get().getDbProductState().getState().equals(DBProductState.states.sale)) {
            return false;
        }

        DBProduct dbProduct = optionalDBProduct.get();
        if (amount <= dbProduct.getHighestBid() || dbProduct.getDbUser().getId() == dbUser.getId()) {
            return false;
        }

        DBBid dbBid = null;
        long more;
        long max = -1;
        if (dbProduct.getBids() != null) {
            for (DBBid dbBid1 : dbProduct.getBids()) {
                if (dbBid1.getDbUser().getId() == dbUser.getId() && dbBid1.getAmount() > max) {
                    max = dbBid1.getAmount();
                    dbBid = dbBid1;
                }
            }
        }


        if (dbBid != null) {

            long total = dbBid.getAmount() + dbUser.getCoins();
            if (amount > total || amount <= dbBid.getAmount()) {
                return false;
            }
            more = amount - dbBid.getAmount();
            dbUser.setCoins(total - amount);
        } else {
            if (amount > dbUser.getCoins()) {
                return false;
            }
            more = amount;
            dbUser.setCoins(dbUser.getCoins() - amount);

        }

        dbBid = new DBBid();
        dbBid.setAmount(amount);
        dbBid.setDbUser(dbUser);
        dbBid.setAmountMore(more);
        dbBid.setDbProduct(dbProduct);
        dbProduct.getBids().add(dbBid);
        dbProduct.setNumberOfBids(dbProduct.getNumberOfBids() + 1);
        dbProduct.setHighestBid(dbBid.getAmount());

        productRepository.save(dbProduct);
        userRepository.save(dbUser);
        return true;
    }

    @Transactional
    public void endBids(Date date) {

        List<DBProduct> list = productRepository.findAll();
        for (DBProduct dbProduct : list) {
            if (dbProduct.getDbProductState().getState().equals(DBProductState.states.sale)) {
                long secs = (date.getTime() -  dbProduct.getDate().getTime()) / 1000;
                long hours = secs / 3600;
                if (hours >= 24) {
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

                        // need to this or else throws error
                        List<DBBid> copy = new ArrayList<>(dbProduct.getBids());
                        for (DBBid dbBid : copy) {
                            dbProduct.removeBid(dbBid);
                        }

                        DBProductState dbProductState = productStateRepository.findByState(DBProductState.states.inventory);
                        dbProduct.setDbProductState(dbProductState);


                        dbProduct.setPrice(dbProduct.getHighestBid());

                        oldUser.setCoins(oldUser.getCoins() + dbProduct.getHighestBid());

                        oldUser.removeProduct(dbProduct);
                        newUser.getProducts().add(dbProduct);
                        dbProduct.setDbUser(newUser);

                        userRepository.save(newUser);
                        userRepository.save(oldUser);
                    }
                    else  {
                        oldUser.setCoins(oldUser.getCoins() + dbProduct.getPrice());
                        DBProductState dbProductState = productStateRepository.findByState(DBProductState.states.inventory);
                        dbProduct.setDbProductState(dbProductState);
                        dbProduct.setHighestBid(dbProduct.getPrice());
                        userRepository.save(oldUser);
                    }
                }
            }
        }
    }

    public boolean sell(String username, long id, long amount, Date date) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return false;
        }
        DBUser dbUser = optionalDBUser.get();
        for (DBProduct dbProduct : dbUser.getProducts()) {
            if (dbProduct.getId() == id) {
                if (dbProduct.getDbProductState().getState().equals(DBProductState.states.sale)) {
                    return false;
                }
                dbProduct.setHighestBid(amount);
                dbProduct.setNumberOfBids(0);

                DBProductState dbProductState = productStateRepository.findByState(DBProductState.states.sale);
                dbProduct.setDbProductState(dbProductState);
                dbProduct.setDate(date);
                userRepository.save(dbUser);
                return true;
            }
        }
        return false;
    }


    public boolean save(String username, String title, List<Long> cats, long cost, MultipartFile file, Date date, strategy strategy) {
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
            DBStoreImage dbStoreImage;
            if (strategy.equals(StoreService.strategy.normal)) {
                dbStoreImage = modifyFile(file);
            } else {
                dbStoreImage = new DBStoreImage();
            }


            DBProduct dbProduct = new DBProduct();
            dbProduct.setDbUser(dbUser);
            dbProduct.setDbStoreImage(dbStoreImage);
            dbStoreImage.setDbProduct(dbProduct);

            dbProduct.setPrice(cost);
            dbProduct.setTitle(title);
            dbProduct.setDbStoreCats(catsList);
            dbProduct.setDate(date);

            DBProductState dbProductState = productStateRepository.findByState(DBProductState.states.sale);
            dbProduct.setDbProductState(dbProductState);

            dbProduct.setBids(new ArrayList<>());
            dbProduct.setHighestBid(cost);
            dbProduct.setNumberOfBids(0);

            dbUser.getProducts().add(dbProduct);
            dbUser.setCoins(dbUser.getCoins() - cost);

            userRepository.save(dbUser);
            return true;
        } catch (IOException ioException) {
            return false;
        }
    }

    public DBStoreImage modifyFile(MultipartFile file) throws IOException {

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
