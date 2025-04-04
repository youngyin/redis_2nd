package yin.domain

enum class ReservationStatus {
    /**
     * 사용자가 좌석을 선택하고 예매 요청을 보낸 상태
     * 결제 대기 중
     */
    PENDING,

    /**
     * 결제가 완료되어 예매가 확정된 상태
     * 좌석이 확정됨
     */
    CONFIRMED,

    /**
     * 사용자가 예매를 취소한 상태
     * 좌석이 다시 예약 가능 상태로 돌아감
     */
    CANCELED,

    /**
     * 결제까지 완료했지만, 시스템 또는 외부 이슈로 인해 예매 확정이 실패한 상태
     * 예외 처리를 위한 보류 상태로 활용
     */
    FAILED
}