package org.example.cardgame.application.queries.adapter.repo;



import org.example.cardgame.application.queries.adapter.repo.model.JuegoListViewModel;
import org.example.cardgame.application.queries.adapter.repo.model.MazoViewModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface MazoViewModelRepository extends ReactiveCrudRepository<MazoViewModel, String> {

    Mono<MazoViewModel> findByJuegoIdAndUid(String juegoId, String uid);
}
