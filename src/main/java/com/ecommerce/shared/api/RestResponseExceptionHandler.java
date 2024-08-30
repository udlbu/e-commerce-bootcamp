package com.ecommerce.shared.api;

import com.ecommerce.cart.domain.exceptions.CartNotFoundException;
import com.ecommerce.cart.domain.exceptions.OfferNotFoundException;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeActivatedException;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeDeletedException;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeModifiedException;
import com.ecommerce.order.domain.exceptions.InactiveOfferException;
import com.ecommerce.order.domain.exceptions.OfferNotAvailableOnStockException;
import com.ecommerce.product.domain.exceptions.ProductCannotBeDeletedException;
import com.ecommerce.product.domain.exceptions.ProductCannotBeModifiedException;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.ErrorCode;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.exception.ConcurrentModification;
import com.ecommerce.shared.domain.exception.UnauthorizedAccessException;
import com.ecommerce.user.domain.exceptions.IAMException;
import com.ecommerce.user.domain.exceptions.IAMUserAlreadyExistException;
import com.ecommerce.user.domain.exceptions.InactiveAccountException;
import com.ecommerce.user.domain.exceptions.PasswordDoesNotMatchException;
import jakarta.ws.rs.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.unprocessableEntity;

@ControllerAdvice
public class RestResponseExceptionHandler {

    private final static Logger LOG = LoggerFactory.getLogger(RestResponseExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        LOG.error("Validation exception: ", ex);
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new Error(ErrorCode.FIELD_REQUIRED, fieldName, errorMessage));
        });
        return badRequest().body(new Errors(errors));
    }

    @ExceptionHandler(ConcurrentModification.class)
    public ResponseEntity<Errors> handleConcurrentModificationExceptions(
            ConcurrentModification ex) {
        LOG.error("Concurrent modification: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.CONCURRENT_MODIFICATION_ERROR, ex.getMessage(), null));
        return internalServerError().body(new Errors(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Errors> handleExceptions(Exception ex) {
        LOG.error("Unexpected server error: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.SERVER_ERROR, ex.getMessage(), null));
        return internalServerError().body(new Errors(errors));
    }

    @ExceptionHandler(value = OfferNotFoundException.class)
    public ResponseEntity<Errors> handleOfferNotFoundException(OfferNotFoundException ex) {
        return handleNotFound(ex, ErrorCode.OFFER_NOT_FOUND);
    }

    @ExceptionHandler(value = CartNotFoundException.class)
    public ResponseEntity<Errors> handleCartNotFoundException(CartNotFoundException ex) {
        return handleNotFound(ex, ErrorCode.CART_NOT_FOUND);
    }

    @ExceptionHandler(InactiveOfferException.class)
    public ResponseEntity<Errors> handleInactiveOfferException(
            InactiveOfferException ex) {
        LOG.warn("Inactive offer when trying to create order: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.INACTIVE_OFFER_FOR_ORDER, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Errors(errors));
    }

    @ExceptionHandler(OfferNotAvailableOnStockException.class)
    public ResponseEntity<Errors> handleOfferNotAvailableOnStockException(
            OfferNotAvailableOnStockException ex) {
        LOG.warn("Offer run out of stock and can't create order: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.OFFER_RUN_OUT_OF_STOCK, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Errors(errors));
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Errors> handlePasswordDoesNotMatchException(
            PasswordDoesNotMatchException ex) {
        LOG.warn("Cannot change password, old password does not match: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.PASSWORD_DOES_NOT_MATCH, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Errors> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex) {
        LOG.warn("Forbidden access: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.FORBIDDEN_ACCESS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Errors(errors));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Errors> handleNotAuthorizedException(
            NotAuthorizedException ex) {
        LOG.warn("Not authorized access: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.UNAUTHORIZED_ACCESS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors(errors));
    }

    @ExceptionHandler(IAMException.class)
    public ResponseEntity<Errors> handleIAMException(IAMException ex) {
        LOG.error("IAM error: ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.IAM_ERROR, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Errors(errors));
    }

    @ExceptionHandler(IAMUserAlreadyExistException.class)
    public ResponseEntity<Errors> handleIAMUserAlreadyExistException(IAMUserAlreadyExistException ex) {
        LOG.warn("Trying to register user with existing email", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.IAM_USER_ALREADY_EXISTS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<Errors> handleInactiveAccountException(InactiveAccountException ex) {
        LOG.warn("Trying to log in but account is inactive", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.INACTIVE_ACCOUNT, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Errors(errors));
    }

    @ExceptionHandler(OfferCannotBeActivatedException.class)
    public ResponseEntity<Errors> handleOfferCannotBeActivatedException(OfferCannotBeActivatedException ex) {
        LOG.warn("Trying to activate offer with empty stock", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.OFFER_RUN_OUT_OF_STOCK, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(ProductCannotBeDeletedException.class)
    public ResponseEntity<Errors> handleProductCannotBeDeletedException(ProductCannotBeDeletedException ex) {
        LOG.warn("Trying to delete product with existing offer", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.OFFER_WITH_PRODUCT_EXITS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(ProductCannotBeModifiedException.class)
    public ResponseEntity<Errors> handleProductCannotBeModifiedException(ProductCannotBeModifiedException ex) {
        LOG.warn("Trying to delete product with existing offer", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.ORDER_WITH_PRODUCT_EXITS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(OfferCannotBeDeletedException.class)
    public ResponseEntity<Errors> handleOfferCannotBeDeletedException(OfferCannotBeDeletedException ex) {
        LOG.warn("Trying to delete offer with existing order", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.ORDER_WITH_OFFER_EXITS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    @ExceptionHandler(OfferCannotBeModifiedException.class)
    public ResponseEntity<Errors> handleOfferCannotBeModifiedException(OfferCannotBeModifiedException ex) {
        LOG.warn("Trying to modify (product or price) offer with existing order", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.ORDER_WITH_OFFER_EXITS, ex.getMessage(), null));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Errors(errors));
    }

    private ResponseEntity<Errors> handleNotFound(RuntimeException ex, String errorCode) {
        LOG.error(errorCode.toLowerCase() + " ", ex);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(errorCode, ex.getMessage(), null));
        return unprocessableEntity().body(new Errors(errors));
    }
}
