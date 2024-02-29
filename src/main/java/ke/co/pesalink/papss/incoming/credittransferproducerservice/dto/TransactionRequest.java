package ke.co.pesalink.papss.incoming.credittransferproducerservice.dto;

import java.time.LocalDateTime;


/**
 * @param adapterRequestId unique identifier of the transaction
 * @param payload   the xml payload containing the transaction information
 * @param localDateTime the time of the transaction
 */
public record TransactionRequest(String adapterRequestId, String payload, LocalDateTime localDateTime) {
}
