package com.bigone.restaurant.serviceImpl;

import com.bigone.restaurant.JWT.CustomerUserDetailsService;
import com.bigone.restaurant.JWT.JwtFilter;
import com.bigone.restaurant.POJO.Bill;
import com.bigone.restaurant.constents.RestaurantConstants;
import com.bigone.restaurant.dao.BillDao;
import com.bigone.restaurant.service.BillService;
import com.bigone.restaurant.utils.EmailUtil;
import com.bigone.restaurant.utils.RestaurantUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.stream.Stream;


@Slf4j
@Service
public class BillServiceImpl implements BillService {
    @Autowired
    BillDao billDao;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    EmailUtil emailUtil;
    @Autowired
    com.bigone.restaurant.JWT.JwtUtil jwtUtil;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Insert generateReport");
      try {
          String filename;
          if (validateResquestMap(requestMap)){
              if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                filename=(String) requestMap.get("uuid");

              }else{
                  filename = RestaurantUtils.getUUID();
                  requestMap.put("uuid", filename);
                  insertBill(requestMap);
              }
              // print user data (name , email m contactNumber , ...)
              String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: " + requestMap.get("contactNumber") +
                      "\n" + "Email: " + requestMap.get("email") + "\n" + "Payment Method: " + requestMap.get("paymentMethod");
               Document document=new Document();
              PdfWriter.getInstance(document, new FileOutputStream(RestaurantConstants.STORE_LOCATION + "\\" + filename + ".pdf"));

              document.open();
              setRectaangleInPdf(document);

              // print pdf Header
              Paragraph chunk = new Paragraph("Restaurant Mohamed Lahbabi",getFont("Header"));
              chunk.setAlignment(Element.ALIGN_CENTER);
              document.add(chunk);

              Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
              document.add(paragraph);

              // Create table in pdf to print data
              PdfPTable table=new PdfPTable(5);
              table.setWidthPercentage(100);
              addTableHeader(table);

              JSONArray jsonArray=RestaurantUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
              for (int i = 0; i < jsonArray.length(); i++) {
                  addRows(table, RestaurantUtils.getMapFromJson(jsonArray.getString(i)));
              }

              document.add(table);

          }
          return RestaurantUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);
      }catch (Exception ex){
          ex.printStackTrace();
      }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dareFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dareFont.setStyle(Font.BOLD);
                return dareFont;
            default:
                return new Font();
        }
    }


    private void setRectaangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectaangleInPdf.");
        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);
    }

    private boolean validateResquestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUsername());
            billDao.save(bill);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
