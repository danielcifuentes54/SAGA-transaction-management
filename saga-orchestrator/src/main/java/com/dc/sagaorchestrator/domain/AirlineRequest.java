package com.dc.sagaorchestrator.domain;

import com.dc.sagaorchestrator.models.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineRequest  implements Serializable {

    private static final long serialVersionUID = -3090843254968408522L;

    private Order order;
}
