package com.mockproject.group3.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.mockproject.group3.config.Config;
import com.mockproject.group3.dto.PaymentDTO;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Payment;
import com.mockproject.group3.model.PaymentDetail;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.PaymentDetailRepository;
import com.mockproject.group3.repository.PaymentRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final GetAuthUserInfo getAuthUserInfo;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, CourseRepository courseRepository, PaymentDetailRepository paymentDetailRepository, GetAuthUserInfo getAuthUserInfo, StudentRepository studentRepository, InstructorRepository instructorRepository) {
        this.paymentRepository = paymentRepository;
        this.courseRepository = courseRepository;
        this.paymentDetailRepository = paymentDetailRepository;
        this.getAuthUserInfo = getAuthUserInfo;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> createPayment(int id) throws UnsupportedEncodingException{
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        long amount =  (long) (course.getPrice()*100);
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", Config.orderType);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        int student_id = getAuthUserInfo.getAuthUserId();

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setStatus("Ok");
        paymentDTO.setMessage("Success");
        paymentDTO.setUrl(paymentUrl);

        Payment payment = new Payment();
        payment.setAmount(amount/100);
        payment.setComment("Thanh toan don hang:" + vnp_TxnRef);
        payment.setPayment_date(LocalDateTime.now());
        payment.setStatus(Status.PENDING);
        payment.setTxnRef(vnp_TxnRef);
        payment.setStudent(studentRepository.findById(student_id).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND)));
        paymentRepository.save(payment);



        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setCourses(course);
        paymentDetail.setPayment(payment);
        paymentDetailRepository.save(paymentDetail);
        return ResponseEntity.ok(paymentDTO);


    }


    public ResponseEntity<?> handleVnpayReturn(Map<String, String> allParams) {
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");

        if ("00".equals(vnp_ResponseCode)) {
            Payment payment = paymentRepository.findByTxnRef(vnp_TxnRef);
            if (payment != null) {
                payment.setStatus(Status.APPROVED);
                paymentRepository.save(payment);

                // Tìm Instructor của khóa học đã thanh toán
                PaymentDetail paymentDetail = paymentDetailRepository.findByPaymentId(payment.getId()).orElseThrow(() -> new AppException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
                Course course = paymentDetail.getCourses();
                Instructor instructor = course.getInstructor();

                instructor.setFee(instructor.getFee() + payment.getAmount());
                instructorRepository.save(instructor);

                logger.info("Payment with TxnRef {} completed successfully", vnp_TxnRef);
                return ResponseEntity.ok("Payment completed successfully");
            } else {
                logger.error("Payment with TxnRef {} not found", vnp_TxnRef);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.PAYMENT_NOT_FOUND);
            }
        } else {
            logger.error("Payment with TxnRef {} failed with response code {}", vnp_TxnRef, vnp_ResponseCode);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCode.PAYMENT_FAILED);
        }
    }

}