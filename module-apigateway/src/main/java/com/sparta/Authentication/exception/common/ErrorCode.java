package com.sparta.Authentication.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	// 400 BAD_REQUEST
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "ERR001", "요청값이 올바르지 않습니다."),
	INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "ERR002", "잘못된 JSON 형식입니다."),
	INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ERR003", "이미 취소 되었거나 결제완료 상태입니다."),
	MISMATCH_REVIEW_PRODUCT(HttpStatus.BAD_REQUEST, "ERR004", "해당 상품의 리뷰가 아닙니다."),
	INVALID_ORDER_CANCELED(HttpStatus.BAD_REQUEST, "ERR005", "주문 취소 기능 요청으로 가능합니다."),
	INVALID_ORDER_COMPLETED(HttpStatus.BAD_REQUEST, "ERR006", "이미 거래가 완료되었습니다."),
	INVALID_FORMAT_TOKEN(HttpStatus.BAD_REQUEST, "ERR007", "지원되지 않는 JWT 토큰입니다."),
	INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "ERR008", "현재 비밀번호가 일치하지 않습니다."),
	INVALID_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, "ERR009", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
	ALREADY_DELETED_USER(HttpStatus.BAD_REQUEST, "ERR010", "이미 탈퇴된 유저입니다."),
	INVALID_ORDER_CREATE(HttpStatus.BAD_REQUEST, "ERR011", "본인의 상품은 주문할 수 없습니다."),
	PAGE_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "ERR012", "페이지 범위를 초과하였습니다."),
    AUTH_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ERR013", "가입한 로그인 방식과 일치하지 않습니다."),
    INVALID_CHATROOM_CREATE(HttpStatus.BAD_REQUEST, "ERR014", "자기자신과 채팅방을 만들 수 없습니다."),
    PAYMENT_ERROR_ORDER_PRICE(HttpStatus.BAD_REQUEST, "ERR015", "요청한 가격과 상품의 가격이 일치하지않습니다."),
	PAYMENT_ERROR_ORDER_NAME(HttpStatus.BAD_REQUEST, "ERR016", "존재하지 않는 결제 방법입니다."),
	INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "ERR017", "유저이름이 일치하지 않습니다."),
	INVALID_RESETCODE(HttpStatus.BAD_REQUEST, "ERR018", "인증코드가 만료되었거나 일치하지 않습니다."),
	AUTH_TYPE_NOT_GENERAL(HttpStatus.BAD_REQUEST, "ERR019", "소셜로그인은 비밀번호 초기화가 불가능합니다."),
	NETWORK_TIMEOUT(HttpStatus.BAD_REQUEST, "ERR022", "서비스가 유효하지 않거나 네트워크 이상이 발생했습니다."),

	// 401 UNAUTHORIZED
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "ERR101", "로그인이 필요합니다."),
	PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "ERR102", "비밀번호가 일치하지 않습니다."),
	INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "ERR103", "유효하지 않는 JWT 서명입니다."),
	EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "ERR104", "만료된 JWT 토큰입니다."),

	// 403 FORBIDDEN
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "ERR201", "접근 권한이 없습니다."),
	COMPLETED_ORDER_REQUIRED(HttpStatus.FORBIDDEN, "ERR202", "해당 상품을 거래 완료한 사용자만 리뷰를 작성할 수 있습니다."),

	// 404 NOT_FOUND
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR301", "사용자를 찾을 수 없습니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR302", "상품을 찾을 수 없습니다."),
	TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR303", "토큰을 찾을 수 없습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR304", "주문을 찾을 수 없습니다."),
	GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR305", "게임을 찾을 수 없습니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR306", "리뷰를 찾을 수 없습니다."),
	ERRORCODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR307", "에러코드를 찾을 수 없습니다."),
	CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR308", "채팅방을 찾을 수 없습니다"),
	BILL_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR309", "거래내역을 찾을 수 없습니다."),
	COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR310", "커뮤니티를 찾을 수 없습니다."),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR311", "결제정보를 찾을 수 없습니다."),

	ALERT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR312", "알림메시지를 찾을 수 없습니다."),
	ALERT_LOG_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERR313", "알림메시지로그를 찾을 수 없습니다."),

	// 409 CONFLICT
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "ERR401", "이미 사용 중인 이메일입니다."),
	DUPLICATE_OUT_OF_AMOUNT(HttpStatus.CONFLICT, "ERR402", "재고가 소진되었습니다."),
	DUPLICATE_CANCELED_ORDER(HttpStatus.CONFLICT, "ERR403", "이미 주문취소가 되었습니다."),
	DUPLICATE_REVIEW(HttpStatus.CONFLICT, "ERR404", "이미 리뷰를 작성하였습니다."),
	REVIEW_ISDELETED(HttpStatus.CONFLICT, "ERR405", "이미 삭제된 리뷰입니다."),
	PRODUCT_ISDELETED(HttpStatus.CONFLICT, "ERR406", "이미 삭제된 상품입니다."),
	GAME_ISDELETED(HttpStatus.CONFLICT, "ERR407", "이미 삭제된 게임입니다."),
	CONFLICT_LOCK_GET(HttpStatus.CONFLICT, "ERR408", "락 획득 실패"),
	CONFLICT_LOCK_ERROR(HttpStatus.CONFLICT, "ERR409", "락 획득 중 오류 발생"),
	DUPLICATE_CHATROOM(HttpStatus.CONFLICT, "ERR410", "이미 채팅방이 존재합니다."),
	PARTICIPANT_ISDELETED(HttpStatus.CONFLICT, "ERR411", "이미 나간 채팅방입니다."),
	DUPLICATE_DELETED_BILL(HttpStatus.CONFLICT, "ERR412", "삭제된 결제내역입니다."),
	COMMUNITY_ISDELETED(HttpStatus.CONFLICT, "ERR413", "이미 삭제된 커뮤니티입니다."),
	DB_ERROR_SAVE(HttpStatus.CONFLICT, "ERR414", "데이터 저장 중 오류가 발생했습니다."),
	CONFLICT_PRICE_EQUALS(HttpStatus.CONFLICT, "ERR415", "요청한 금액이 결제 금액과 맞지않습니다."),
	PAYMENT_CANCELED_OK(HttpStatus.CONFLICT,"ERR416", "이미 취소 완료되었습니다." ),
	PAYMENT_PENDING(HttpStatus.CONFLICT,"ERR417", "아직 결제되지 않은 상품입니다."),
	INVALID_REQUEST_MANY(HttpStatus.CONFLICT, "ERR418", "취소 요청이 너무 많습니다. 잠시 후 다시 시도해주세요." ),

	// 500 INTERNAL_SERVER_ERROR
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR501", "서버 내부 오류가 발생했습니다."),

	// 424 FAIL
	PAYMENT_FAILED_RETRY(HttpStatus.FAILED_DEPENDENCY, "ERR601", "승인 요청을 반복적으로 실패했습니다."),
	PAYMENT_FAILED(HttpStatus.FAILED_DEPENDENCY, "ERR601", "승인 요청을 실패했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public static ErrorCode from(String message) {
		for (ErrorCode errorCode : values()) {
			if (errorCode.getMessage().equals(message)) {
				return errorCode;
			}
		}
		throw new NotFoundException(ERRORCODE_NOT_FOUND);
	}

}
