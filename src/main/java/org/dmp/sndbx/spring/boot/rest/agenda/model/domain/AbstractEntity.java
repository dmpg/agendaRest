package org.dmp.sndbx.spring.boot.rest.agenda.model.domain;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class AbstractEntity {
   @Id
   @GeneratedValue
   @JsonIgnore
   // @JsonIgnore is to be ignored by introspection-based serialization and
   // deserialization functionality. That is, it should not be consider a
   // "getter", "setter" or "creator".
   private Long id;

   // ETag
   @Version
   @JsonIgnore
   private Long version;

   @JsonIgnore
   @LastModifiedDate
   @Temporal(TemporalType.TIMESTAMP)
   @DateTimeFormat(iso = ISO.DATE_TIME)
   // @DateTimeFormat(pattern="dd/MM/yyyy")
   private Date lastModifiedDate;

   @JsonIgnore
   @CreatedDate
   @Temporal(TemporalType.TIMESTAMP)
   @DateTimeFormat(iso = ISO.DATE_TIME)
   private Date createdDate;

   public Date getCreatedDate() {
      return createdDate;
   }

   public Date getLastModifiedDate() {
      return lastModifiedDate;
   }

   public Long getVersion() {
      if (this.version == null) {
         this.version = 0L;
      }

      return version;
   }

   public Long getId() {
      return id;
   }

   @PrePersist
   public void prePersist() {
      // setCreatedBy(currentUser);
      createdDate = new Date();
   }

   @PreUpdate
   public void preUpdate() {
      // setLastModifiedBy(currentUser);
      lastModifiedDate = new Date();
   }
}
