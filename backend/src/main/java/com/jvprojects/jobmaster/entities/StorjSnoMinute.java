package com.jvprojects.jobmaster.entities;

import com.jvprojects.jobmaster.entities.common.StorjSnoTimes;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "storj_sno_minute")
public class StorjSnoMinute extends StorjSnoTimes {
}
