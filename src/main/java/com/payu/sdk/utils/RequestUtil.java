/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 developers-payu-latam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.payu.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.payu.sdk.PayU;
import com.payu.sdk.constants.Constants;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.exceptions.SDKException.ErrorCode;
import com.payu.sdk.helper.SignatureHelper;
import com.payu.sdk.model.Address;
import com.payu.sdk.model.BankListInformation;
import com.payu.sdk.model.Buyer;
import com.payu.sdk.model.CreditCard;
import com.payu.sdk.model.CreditCardToken;
import com.payu.sdk.model.CreditCardTokenInformation;
import com.payu.sdk.model.Currency;
import com.payu.sdk.model.DocumentType;
import com.payu.sdk.model.ExtraParemeterNames;
import com.payu.sdk.model.Merchant;
import com.payu.sdk.model.Order;
import com.payu.sdk.model.Payer;
import com.payu.sdk.model.PaymentCountry;
import com.payu.sdk.model.PaymentMethod;
import com.payu.sdk.model.Person;
import com.payu.sdk.model.PersonType;
import com.payu.sdk.model.RemoveCreditCardToken;
import com.payu.sdk.model.Transaction;
import com.payu.sdk.model.TransactionSource;
import com.payu.sdk.model.TransactionType;
import com.payu.sdk.model.request.Command;
import com.payu.sdk.model.request.CommandRequest;
import com.payu.sdk.model.request.Request;
import com.payu.sdk.payments.model.CreditCardTokenListRequest;
import com.payu.sdk.payments.model.CreditCardTokenRequest;
import com.payu.sdk.payments.model.PaymentMethodRequest;
import com.payu.sdk.payments.model.PaymentRequest;
import com.payu.sdk.payments.model.RemoveCreditCardTokenRequest;
import com.payu.sdk.reporting.model.ReportingRequest;

/**
 * Utility for requests in the PayU SDK.
 *
 * @author PayU Latam
 * @since 1.0.0
 * @version 1.0.0, 21/08/2013
 */
public final class RequestUtil extends CommonRequestUtil {

	/** The encoding used to send confirmation page */
	private static final String ENCODING = Constants.DEFAULT_ENCODING
			.toString();
	/** The character to append parameters */
	private static final String APPENDER = "&";
	/** The character to assign a value param */
	private static final String EQUALS = "=";

	/**
	 * Private Constructor
	 */
	private RequestUtil() {
	}

	/**
	 * Builds a payments ping request
	 *
	 * @return The complete payments ping request
	 */
	public static PaymentRequest buildPaymentsPingRequest() {

		PaymentRequest request = buildDefaultPaymentRequest();
		request.setCommand(Command.PING);
		return request;

	}

	/**
	 * Builds a reporting ping request
	 *
	 * @return The complete reporting request to be sent to the server
	 */
	public static ReportingRequest buildReportingPingRequest() {

		ReportingRequest request = buildDefaultReportingRequest();
		request.setCommand(Command.PING);
		return request;

	}

	/* Payment Requests */

	/**
	 * Builds a get bank list request
	 *
	 * @param paymentCountry
	 *            The country in which the transaction is being done
	 * @return The complete bank list request
	 */
	public static Request buildBankListRequest(PaymentCountry paymentCountry) {

		PaymentRequest request = buildDefaultPaymentRequest();
		request.setCommand(Command.GET_BANKS_LIST);

		request.setBankListInformation(new BankListInformation(
				PaymentMethod.PSE, paymentCountry));

		return request;
	}

	/**
	 * Builds a payment request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @param transactionType
	 *            The transaction that is being done
	 * @return The complete payment request
	 * @throws InvalidParametersException
	 */
	public static Request buildPaymentRequest(Map<String, String> parameters,
			TransactionType transactionType) throws InvalidParametersException {

		PaymentRequest request = buildDefaultPaymentRequest();
		request.setCommand(Command.SUBMIT_TRANSACTION);

		request.setTransaction(buildTransaction(parameters, transactionType));

		return request;
	}

	/**
	 * Builds the payment method list request
	 *
	 * @return The complete payment methods list request
	 */
	public static Request buildPaymentMethodsListRequest() {

		PaymentRequest request = buildDefaultPaymentRequest();
		request.setCommand(Command.GET_PAYMENT_METHODS);

		return request;
	}

	/**
	 * Builds the payment method request
	 * @return
	 */
	public static Request buildPaymentMethodAvailability(String paymentMethod) {
		PaymentMethodRequest request = new PaymentMethodRequest();
		request = (PaymentMethodRequest) buildDefaultRequest(request);
		request.setTest(PayU.isTest);
		request.setCommand(Command.GET_PAYMENT_METHOD_AVAILABILITY);
		request.setPaymentMethod(paymentMethod);
		return request;
	}

	/* Reporting Requests */

	/**
	 * Builds a order details reporting by the id
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete reporting request to be sent to the server
	 * @throws InvalidParametersException
	 */
	public static ReportingRequest buildOrderReportingDetails(
			Map<String, String> parameters) throws InvalidParametersException {

		ReportingRequest request = buildDefaultReportingRequest();
		request.setCommand(Command.ORDER_DETAIL);

		Integer orderId = getIntegerParameter(parameters,
				PayU.PARAMETERS.ORDER_ID);

		Map<String, Object> details = new HashMap<String, Object>();
		details.put(PayU.PARAMETERS.ORDER_ID, orderId);

		request.setDetails(details);

		return request;
	}

	/**
	 * Builds a order details reporting by reference code
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete reporting request to be sent to the server
	 */
	public static ReportingRequest buildOrderReportingByReferenceCode(
			Map<String, String> parameters) {

		ReportingRequest request = buildDefaultReportingRequest();
		request.setCommand(Command.ORDER_DETAIL_BY_REFERENCE_CODE);

		request.setDetails(new HashMap<String, Object>(parameters));

		return request;
	}

	/**
	 * Builds a transaction reporting by the id
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete reporting request to be sent to the server
	 */
	public static ReportingRequest buildTransactionResponse(
			Map<String, String> parameters) {

		ReportingRequest request = buildDefaultReportingRequest();
		request.setCommand(Command.TRANSACTION_RESPONSE_DETAIL);

		request.setDetails(new HashMap<String, Object>(parameters));

		return request;
	}

	/* Token Requests */

	/**
	 * Builds a create credit card token request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete create credit card token request
	 * @throws InvalidParametersException
	 */
	public static Request buildCreateTokenRequest(Map<String, String> parameters)
			throws InvalidParametersException {

		String nameOnCard = getParameter(parameters, PayU.PARAMETERS.PAYER_NAME);

		String payerId = getParameter(parameters, PayU.PARAMETERS.PAYER_ID);

		String dni = getParameter(parameters, PayU.PARAMETERS.PAYER_DNI);

		String creditCardNumber = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_NUMBER);

		String expirationDate = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE);

		PaymentMethod paymentMethod = getEnumValueParameter(
				PaymentMethod.class, parameters, PayU.PARAMETERS.PAYMENT_METHOD);

		CreditCardTokenRequest request = new CreditCardTokenRequest();
		request = (CreditCardTokenRequest) buildDefaultRequest(request);
		request.setCommand(Command.CREATE_TOKEN);

		request.setCreditCardToken(buildCreditCardToken(nameOnCard, payerId,
				dni, paymentMethod, creditCardNumber, expirationDate));

		return request;
	}

	/**
	 * Builds a get credit card token request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete get credit card token request
	 * @throws InvalidParametersException
	 */
	public static Request buildGetCreditCardTokensRequest(
			Map<String, String> parameters) throws InvalidParametersException {

		String payerId = getParameter(parameters, PayU.PARAMETERS.PAYER_ID);

		String tokenId = getParameter(parameters, PayU.PARAMETERS.TOKEN_ID);

		String strStartDate = getParameter(parameters,
				PayU.PARAMETERS.START_DATE);

		String strEndDate = getParameter(parameters, PayU.PARAMETERS.END_DATE);

		validateDateParameter(strStartDate, PayU.PARAMETERS.START_DATE,
				Constants.DEFAULT_DATE_FORMAT);
		validateDateParameter(strEndDate, PayU.PARAMETERS.END_DATE,
				Constants.DEFAULT_DATE_FORMAT);

		CreditCardTokenListRequest request = new CreditCardTokenListRequest();
		request = (CreditCardTokenListRequest) buildDefaultRequest(request);
		request.setCommand(Command.GET_TOKENS);

		CreditCardTokenInformation information = new CreditCardTokenInformation();

		information.setPayerId(payerId);
		information.setTokenId(tokenId);

		information.setStartDate(strStartDate);
		information.setEndDate(strEndDate);

		request.setCreditCardTokenInformation(information);

		return request;
	}

	/**
	 * Builds a remove credit card token request
	 *
	 * @param parameters
	 *            The parameters to be sent to the server
	 * @return The complete remove credit card token request
	 */
	public static Request buildRemoveTokenRequest(Map<String, String> parameters) {

		String payerId = getParameter(parameters, PayU.PARAMETERS.PAYER_ID);

		String tokenId = getParameter(parameters, PayU.PARAMETERS.TOKEN_ID);

		RemoveCreditCardTokenRequest request = new RemoveCreditCardTokenRequest();
		request = (RemoveCreditCardTokenRequest) buildDefaultRequest(request);
		request.setCommand(Command.REMOVE_TOKEN);

		RemoveCreditCardToken remove = new RemoveCreditCardToken();

		remove.setPayerId(payerId);
		remove.setCreditCardTokenId(tokenId);

		request.setRemoveCreditCardToken(remove);

		return request;
	}

	/* PRIVATE METHODS */

	/* Default Requests Methods */

	/**
	 * Builds a default request
	 *
	 * @return A simple request with merchant and language
	 */
	private static Request buildDefaultRequest(CommandRequest request) {
		request.setMerchant(buildMerchant());
		request.setLanguage(PayU.language);
		return request;
	}

	/**
	 * Builds the default payment request
	 *
	 * @return A simple payment request with merchant, language and test
	 */
	private static PaymentRequest buildDefaultPaymentRequest() {
		PaymentRequest request = new PaymentRequest();
		request = (PaymentRequest) buildDefaultRequest(request);
		request.setTest(PayU.isTest);
		return request;
	}

	/**
	 * Builds the default reporting request
	 *
	 * @return A simple reporting request with merchant, language and test
	 */
	private static ReportingRequest buildDefaultReportingRequest() {
		ReportingRequest request = new ReportingRequest();
		request = (ReportingRequest) buildDefaultRequest(request);
		request.setTest(PayU.isTest);
		return request;
	}

	/**
	 * Builds a credit card entity
	 *
	 * @param name
	 *            The credit card owner's name
	 * @param creditCardNumber
	 *            The credit card's number
	 * @param expirationDate
	 *            The credit card's expiration date
	 * @param securityCode
	 *            The credit card's security code
	 * @return The credit cards built
	 */
	private static CreditCard buildCreditCard(String name,
			String creditCardNumber, String expirationDate,
			Boolean processWithoutCvv2, String securityCode) {

		if (creditCardNumber != null || processWithoutCvv2 != null
				|| securityCode != null) {

			CreditCard creditCard = new CreditCard();
			creditCard.setName(name);
			creditCard.setNumber(creditCardNumber);
			creditCard.setExpirationDate(expirationDate);
			creditCard.setProcessWithoutCvv2(processWithoutCvv2);
			creditCard.setSecurityCode(securityCode);

			return creditCard;
		}
		return null;
	}

	/**
	 * builds credit card token
	 *
	 * @param nameOnCard
	 *            the credit card owner's name
	 * @param payerId
	 *            the payer's id
	 * @param payerIdentificationNumber
	 *            the payer's identification number
	 * @param paymentMethod
	 *            the payment method being used
	 * @param creditCardNumber
	 *            the credit card's number
	 * @param expirationDate
	 *            the credit card's expiration date
	 * @return The credit card token built
	 */
	private static CreditCardToken buildCreditCardToken(String nameOnCard,
			String payerId, String payerIdentificationNumber,
			PaymentMethod paymentMethod, String creditCardNumber,
			String expirationDate) {

		CreditCardToken creditCardToken = new CreditCardToken();
		creditCardToken.setName(nameOnCard);
		creditCardToken.setPayerId(payerId);
		creditCardToken.setIdentificationNumber(payerIdentificationNumber);
		creditCardToken.setPaymentMethod(paymentMethod);
		creditCardToken.setExpirationDate(expirationDate);
		creditCardToken.setNumber(creditCardNumber);

		return creditCardToken;
	}

	/**
	 * Builds a credit card transaction
	 *
	 * @param transaction
	 *            The transaction to modify
	 * @param nameOnCard
	 *            The credit card owner's name
	 * @param creditCardNumber
	 *            The credit card's number
	 * @param expirationDate
	 *            The credit card's expiration date
	 * @param securityCode
	 *            The credit card's security code
	 * @param installments
	 *            The number of installments for the transaction
	 * @param createCreditCardToken
	 * @throws InvalidParametersException
	 */
	private static void buildCreditCardTransaction(Transaction transaction,
			String nameOnCard, String creditCardNumber, String expirationDate,
			Boolean processWithoutCvv2, String securityCode,
			Integer installments, Boolean createCreditCardToken)
			throws InvalidParametersException {

		transaction.setCreditCard(buildCreditCard(nameOnCard, creditCardNumber,
				expirationDate, processWithoutCvv2, securityCode));

		if (installments != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.INSTALLMENTS_NUMBER.name(),
					installments.toString());
		}

		transaction.setCreateCreditCardToken(createCreditCardToken);

	}

	/**
	 * Builds a merchant entity
	 *
	 * @return The merchant entity built
	 */
	private static Merchant buildMerchant() {

		Merchant merchant = new Merchant();
		merchant.setApiKey(PayU.apiKey);
		merchant.setApiLogin(PayU.apiLogin);

		return merchant;
	}

	/**
	 * Builds the order
	 *
	 * @param accountId
	 *            The account's id number
	 * @param txCurrency
	 *            The currency of the transaction
	 * @param txValue
	 *            The value of the transaction
	 * @param description
	 *            The description of the transaction
	 * @param referenceCode
	 *            The order's reference code
	 * @param notifyUrl
	 * 			  The confirmation page URL
	 * @return The order built
	 */
	private static Order buildOrder(Integer accountId, Currency txCurrency,
			BigDecimal txValue, BigDecimal taxValue, BigDecimal taxReturnBase,
			String description, String referenceCode, String notifyUrl) {

		Order order = new Order();
		order.setAccountId(accountId);
		order.setDescription(description);
		order.setLanguage(PayU.language);
		order.setReferenceCode(referenceCode);
		order.setNotifyUrl(notifyUrl);
		order.setAdditionalValues(buildAdditionalValues(txCurrency, txValue,
				taxValue, taxReturnBase));

		return order;
	}

	/**
	 * Builds the buyer entity
	 *
	 * @param parameters
	 *            The parameters map to build the buyer
	 * @return The buyer built
	 */
	private static Buyer buildBuyer(Map<String, String> parameters) {

		String buyerId = getParameter(parameters, PayU.PARAMETERS.BUYER_ID);
		String buyerEmail = getParameter(parameters,
				PayU.PARAMETERS.BUYER_EMAIL);
		String buyerName = getParameter(parameters, PayU.PARAMETERS.BUYER_NAME);
		String buyerCNPJ = getParameter(parameters, PayU.PARAMETERS.BUYER_CNPJ);
		String buyerContactPhone = getParameter(parameters,
				PayU.PARAMETERS.BUYER_CONTACT_PHONE);
		String buyerDniNumber = getParameter(parameters,
				PayU.PARAMETERS.BUYER_DNI);
		String buyerCity = getParameter(parameters, PayU.PARAMETERS.BUYER_CITY);
		String buyerCountry = getParameter(parameters,
				PayU.PARAMETERS.BUYER_COUNTRY);
		String buyerPhone = getParameter(parameters,
				PayU.PARAMETERS.BUYER_PHONE);
		String buyerPostalCode = getParameter(parameters,
				PayU.PARAMETERS.BUYER_POSTAL_CODE);
		String buyerState = getParameter(parameters,
				PayU.PARAMETERS.BUYER_STATE);
		String buyerStreet = getParameter(parameters,
				PayU.PARAMETERS.BUYER_STREET);
		String buyerStreet2 = getParameter(parameters,
				PayU.PARAMETERS.BUYER_STREET_2);
		String buyerStreet3 = getParameter(parameters,
				PayU.PARAMETERS.BUYER_STREET_3);

		Buyer buyer = new Buyer();
		buildPerson(buyer, buyerId, buyerEmail, buyerName, buyerCNPJ,
				buyerContactPhone, buyerDniNumber, buyerCity, buyerCountry,
				buyerPhone, buyerPostalCode, buyerState, buyerStreet,
				buyerStreet2, buyerStreet3);

		return buyer;
	}

	/**
	 * Builds the payer entity
	 *
	 * @param parameters
	 *            The parameters map to build the payer
	 * @return The payer built
	 * @throws InvalidParametersException
	 */
	private static Payer buildPayer(Map<String, String> parameters)
			throws InvalidParametersException {

		String payerId = getParameter(parameters, PayU.PARAMETERS.PAYER_ID);
		String payerEmail = getParameter(parameters,
				PayU.PARAMETERS.PAYER_EMAIL);
		String payerName = getParameter(parameters, PayU.PARAMETERS.PAYER_NAME);
		String payerCNPJ = getParameter(parameters, PayU.PARAMETERS.PAYER_CNPJ);
		String payerContactPhone = getParameter(parameters,
				PayU.PARAMETERS.PAYER_CONTACT_PHONE);
		String payerDniNumber = getParameter(parameters,
				PayU.PARAMETERS.PAYER_DNI);
		String payerCity = getParameter(parameters, PayU.PARAMETERS.PAYER_CITY);
		String payerCountry = getParameter(parameters,
				PayU.PARAMETERS.PAYER_COUNTRY);
		String payerPhone = getParameter(parameters,
				PayU.PARAMETERS.PAYER_PHONE);
		String payerPostalCode = getParameter(parameters,
				PayU.PARAMETERS.PAYER_POSTAL_CODE);
		String payerState = getParameter(parameters,
				PayU.PARAMETERS.PAYER_STATE);
		String payerStreet = getParameter(parameters,
				PayU.PARAMETERS.PAYER_STREET);
		String payerStreet2 = getParameter(parameters,
				PayU.PARAMETERS.PAYER_STREET_2);
		String payerStreet3 = getParameter(parameters,
				PayU.PARAMETERS.PAYER_STREET_3);

		String payerBusinessName = getParameter(parameters,
				PayU.PARAMETERS.PAYER_BUSINESS_NAME);
		PersonType payerType = getEnumValueParameter(PersonType.class,
				parameters, PayU.PARAMETERS.PAYER_PERSON_TYPE);

		String payerBirthdate = getParameter(parameters,
				PayU.PARAMETERS.PAYER_BIRTH_DATE);
		if (payerBirthdate != null && !payerBirthdate.isEmpty()){
			validateDateParameter(payerBirthdate, PayU.PARAMETERS.PAYER_BIRTH_DATE,
					Constants.DEFAULT_DATE_WITHOUT_HOUR_FORMAT, false);
		}
		Payer payer = new Payer();
		buildPerson(payer, payerId, payerEmail, payerName, payerCNPJ,
				payerContactPhone, payerDniNumber, payerCity, payerCountry,
				payerPhone, payerPostalCode, payerState, payerStreet,
				payerStreet2, payerStreet3);
		payer.setBusinessName(payerBusinessName);
		payer.setPayerType(payerType);
		payer.setBirthdate(payerBirthdate);
		return payer;
	}

	/**
	 * Builds the person entity
	 *
	 * @param person
	 *            The person to build
	 * @param personId
	 *            The person's id in the merchant
	 * @param email
	 *            The person's e-mail
	 * @param name
	 *            The person's name
	 * @param CNPJ
	 *            The person's CNPJ
	 * @param contactPhone
	 *            The person's contact phone
	 * @param dniNumber
	 *            The person's dni number
	 * @param city
	 *            The person's city
	 * @param country
	 *            The person's country
	 * @param phone
	 *            The person's phone
	 * @param postalCode
	 *            The person's postal code
	 * @param state
	 *            The person's state
	 * @param street
	 *            The person's street
	 * @param street2
	 *            The person's street2
	 * @param street3
	 *            The person's street3
	 */
	private static void buildPerson(Person person, String personId,
			String email, String name, String CNPJ, String contactPhone,
			String dniNumber, String city, String country, String phone,
			String postalCode, String state, String street, String street2,
			String street3) {

		person.setMerchantPersonId(personId);
		person.setEmailAddress(email);
		person.setFullName(name);
		person.setCNPJ(CNPJ);

		person.setContactPhone(contactPhone);
		person.setDniNumber(dniNumber);

		Address address = new Address();
		address.setCity(city);
		address.setCountry(country);
		address.setPhone(phone);
		address.setPostalCode(postalCode);
		address.setState(state);
		address.setLine1(street);
		address.setLine2(street2);
		address.setLine3(street3);

		person.setAddress(address);

	}

	/**
	 * Build a transaction request based on the query parameters
	 *
	 * @param parameters
	 *            The parameters map to send to the request
	 * @param transactionType
	 *            The type of payment transaction to build
	 * @return The transaction to be sent built
	 * @throws InvalidParametersException
	 */
	private static Transaction buildTransaction(Map<String, String> parameters,
			TransactionType transactionType) throws InvalidParametersException {

		String payerName = getParameter(parameters, PayU.PARAMETERS.PAYER_NAME);

		Integer orderId = getIntegerParameter(parameters,
				PayU.PARAMETERS.ORDER_ID);
		Integer accountId = getIntegerParameter(parameters,
				PayU.PARAMETERS.ACCOUNT_ID);

		String orderReference = getParameter(parameters,
				PayU.PARAMETERS.REFERENCE_CODE);
		String orderDescription = getParameter(parameters,
				PayU.PARAMETERS.DESCRIPTION);
		String orderNotifyUrl = getParameter(parameters,
				PayU.PARAMETERS.NOTIFY_URL);

		String creditCardNumber = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_NUMBER);
		String creditCardExpirationDate = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE);
		Boolean processWithoutCvv2 = getBooleanParameter(parameters,
				PayU.PARAMETERS.PROCESS_WITHOUT_CVV2);
		String securityCode = getParameter(parameters,
				PayU.PARAMETERS.CREDIT_CARD_SECURITY_CODE);

		Boolean createCreditCardToken = getBooleanParameter(parameters,
				PayU.PARAMETERS.CREATE_CREDIT_CARD_TOKEN);

		String parentTransactionId = getParameter(parameters,
				PayU.PARAMETERS.TRANSACTION_ID);

		String expirationDate = getParameter(parameters,
				PayU.PARAMETERS.EXPIRATION_DATE);

		String cookie = getParameter(parameters, PayU.PARAMETERS.COOKIE);

		PaymentCountry paymentCountry = getEnumValueParameter(
				PaymentCountry.class, parameters, PayU.PARAMETERS.COUNTRY);

		//Obtains the payment method. If the parameter is a value that doesn't belong to the enum, this return null and continue

		PaymentMethod paymentMethod = CommonRequestUtil
				.getPaymentMethodParameter(parameters,PayU.PARAMETERS.PAYMENT_METHOD);

		String reason = getParameter(parameters,
				PayU.PARAMETERS.REASON);

		Currency txCurrency = getEnumValueParameter(Currency.class, parameters,
				PayU.PARAMETERS.CURRENCY);

		BigDecimal txValue = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.VALUE);

		Integer installments = getIntegerParameter(parameters,
				PayU.PARAMETERS.INSTALLMENTS_NUMBER);

		// TAX_VALUE
		BigDecimal taxValue = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.TAX_VALUE);

		// TAX_RETURN_BASE
		BigDecimal taxReturnBase = getBigDecimalParameter(parameters,
				PayU.PARAMETERS.TAX_RETURN_BASE);

		// IP Address
		String ipAddress = getParameter(parameters, PayU.PARAMETERS.IP_ADDRESS);

		// User Agent
		String userAgent = getParameter(parameters, PayU.PARAMETERS.USER_AGENT);

		// Device session ID
		String deviceSessionId = getParameter(parameters,
				PayU.PARAMETERS.DEVICE_SESSION_ID);

		//Response page
		String responseUrlPage = getParameter(parameters, PayU.PARAMETERS.RESPONSE_URL);

		String tokenId = getParameter(parameters, PayU.PARAMETERS.TOKEN_ID);

		Transaction transaction = new Transaction();
		transaction.setType(transactionType);


		if(responseUrlPage != null){

			addResponseUrlPage(transaction, responseUrlPage );
		}


		if (TransactionType.AUTHORIZATION_AND_CAPTURE.equals(transactionType)
				|| TransactionType.AUTHORIZATION.equals(transactionType)) {

			transaction.setPaymentCountry(paymentCountry);

			if (orderId == null) {

				String signature = getParameter(parameters,
						PayU.PARAMETERS.SIGNATURE);
				String merchantId = PayU.merchantId;

				Order order = buildOrder(accountId, txCurrency, txValue,
						taxValue, taxReturnBase, orderDescription,
						orderReference, orderNotifyUrl);

				if (signature == null && merchantId != null) {
					signature = SignatureHelper.buildSignature(order,
							Integer.parseInt(merchantId), PayU.apiKey,
							SignatureHelper.DECIMAL_FORMAT_3,
							SignatureHelper.MD5_ALGORITHM);
				}

				order.setSignature(signature);
				transaction.setOrder(order);

			} else {
				Order order = new Order();
				order.setId(orderId);
				transaction.setOrder(order);
			}

			transaction.getOrder().setBuyer(buildBuyer(parameters));

			transaction.setCookie(cookie);

			// MAF parameters
			transaction.setUserAgent(userAgent);
			transaction.setIpAddress(ipAddress);
			transaction.setDeviceSessionId(deviceSessionId);

			// PSE extra parameters
			if (PaymentMethod.PSE.equals(paymentMethod)) {
				addPSEExtraParameters(transaction, parameters);
			}

			transaction.setSource(TransactionSource.PAYU_SDK);

			if (creditCardNumber != null || tokenId != null) {
				buildCreditCardTransaction(transaction, payerName,
						creditCardNumber, creditCardExpirationDate,
						processWithoutCvv2, securityCode, installments,
						createCreditCardToken);
			}

			if (expirationDate != null) {
				Date expDate = validateDateParameter(expirationDate,
						PayU.PARAMETERS.EXPIRATION_DATE,
						Constants.DEFAULT_DATE_FORMAT);
				transaction.setExpirationDate(expDate);
			}

			transaction.setCreditCardTokenId(tokenId);
			//Set the param of Payment Method
			String paramPaymentMethod = getParameter(parameters, PayU.PARAMETERS.PAYMENT_METHOD);
			transaction.setPaymentMethod(paramPaymentMethod);
			//transaction.setPaymentMethod(paymentMethod);
			transaction.setPayer(buildPayer(parameters));

		} else if (TransactionType.VOID.equals(transactionType)
				|| TransactionType.REFUND.equals(transactionType)
				|| TransactionType.CAPTURE.equals(transactionType)) {

			transaction.setParentTransactionId(parentTransactionId);

			Order order = new Order();
			order.setId(orderId);

			order.setReferenceCode(orderReference);
			order.setDescription(orderDescription);
			order.setLanguage(PayU.language);

			transaction.setAdditionalValues(buildAdditionalValues(txCurrency,
					txValue, taxValue, taxReturnBase));
			transaction.setOrder(order);

			transaction.setReason(reason);
		}

		return transaction;
	}

	/**
	 * Adds the extra parameters required by the PSE payment method
	 *
	 * @param transaction
	 * @param parameters
	 * @throws InvalidParametersException
	 */
	private static void addPSEExtraParameters(Transaction transaction,
			Map<String, String> parameters) throws InvalidParametersException {

		// PSE reference identification 1
		String pseReference1 = getParameter(parameters,
				PayU.PARAMETERS.IP_ADDRESS);

		// PSE reference identification 2
		String pseReference2 = getEnumValueParameter(DocumentType.class,
				parameters, PayU.PARAMETERS.PAYER_DOCUMENT_TYPE).name();

		// PSE reference identification 3
		String pseReference3 = getParameter(parameters,
				PayU.PARAMETERS.PAYER_DNI);

		// PSE user type N-J (Natural or Legal)
		PersonType pseUserType = getEnumValueParameter(PersonType.class,
				parameters, PayU.PARAMETERS.PAYER_PERSON_TYPE);

		// PSE financial institution code (Bank code)
		String pseFinancialInstitutionCode = getParameter(parameters,
				PayU.PARAMETERS.PSE_FINANCIAL_INSTITUTION_CODE);

		// PSE financial institution name (Bank Name)
		String pseFinancialInstitutionName = getParameter(parameters,
				PayU.PARAMETERS.PSE_FINANCIAL_INSTITUTION_NAME);

		if (pseFinancialInstitutionCode != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.FINANCIAL_INSTITUTION_CODE.name(),
					pseFinancialInstitutionCode);
		}

		if (pseFinancialInstitutionName != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.FINANCIAL_INSTITUTION_NAME.name(),
					pseFinancialInstitutionName);
		}

		if (pseUserType != null) {
			transaction.addExtraParameter(ExtraParemeterNames.USER_TYPE.name(),
					pseUserType.getPseCode());
		}

		if (pseReference1 != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.PSE_REFERENCE1.name(), pseReference1);
		}

		if (pseReference2 != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.PSE_REFERENCE2.name(), pseReference2);
		}

		if (pseReference3 != null) {
			transaction.addExtraParameter(
					ExtraParemeterNames.PSE_REFERENCE3.name(), pseReference3);
		}

	}

	public static String mapToString(Map<String, String> map)
			throws PayUException {
		StringBuilder stringBuilder = new StringBuilder();
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (value != null) {
					if (stringBuilder.length() > 0) {
						stringBuilder.append(APPENDER);
					}
					try {
						stringBuilder.append((key != null ? URLEncoder.encode(
								key, ENCODING) : ""));
						stringBuilder.append(EQUALS);
						stringBuilder.append(URLEncoder.encode(
								value, ENCODING));
					} catch (UnsupportedEncodingException e) {
						throw new PayUException(ErrorCode.INVALID_PARAMETERS,
								"can not encode the url");
					}
				}
			}
		}
		return stringBuilder.toString();
	}


	/**
	 * Method for add in the transaccion a {@link ExtraParemeterNames} with the response url page.
	 * <ul>
	 * 	<li>1. Get of the {@link ExtraParemeterNames} RESPONSE_URL.</li>
	 * 	<li>2. sets in the extra parameter of the {@code responseUrl value}.</li>
	 * </ul>
	 *
	 * @param transaction
	 * @param responseUrl
	 * @throws InvalidParametersException
	 */
	private static void addResponseUrlPage(final Transaction transaction, String responseUrl) throws InvalidParametersException{

		transaction.addExtraParameter(ExtraParemeterNames.RESPONSE_URL.name(),
				responseUrl);

	}

}