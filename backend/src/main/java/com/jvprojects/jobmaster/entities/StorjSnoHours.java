package com.jvprojects.jobmaster.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "storj_sno_hourly")
public class StorjSnoHours extends StorjSnoMinutes {
}
