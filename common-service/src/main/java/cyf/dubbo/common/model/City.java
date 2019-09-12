package cyf.dubbo.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Cheng Yufei
 * @create 2019-01-17 14:50
 **/
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class City implements Serializable {

    private Integer id;

    private String cityName;
}
