package com.poppin.poppinserver.visit.domain;

import com.poppin.poppinserver.core.type.ECongestion;
import com.poppin.poppinserver.core.type.ESatisfaction;
import com.poppin.poppinserver.core.type.EVisitDate;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "visitor_data")
public class VisitorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "popup_id", referencedColumnName = "id", nullable = false)
    private Popup popup;

    @Column(name = "visit_date", nullable = false)
    private String visitDate; // WEEKDAY_AM , WEEKDAY_PM , WEEKEND_AM, WEEKEND_PM

    @Column(name = "congestion", nullable = false)
    private String congestion; // 혼잡, 보통, 여유

    @Column(name = "satisfaction", nullable = false)
    private String satisfaction; // 만족, 보통, 불만족

    @Builder
    public VisitorData(Enum<EVisitDate> visitDate, Popup popup, Review review, Enum<ECongestion> congestion,
                       Enum<ESatisfaction> satisfaction) {
        this.visitDate = visitDate.toString();
        this.popup = popup;
        this.review = review;
        this.congestion = congestion.toString();
        this.satisfaction = satisfaction.toString();
    }


}
