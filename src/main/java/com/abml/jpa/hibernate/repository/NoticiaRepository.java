public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByInformacionContainingIgnoreCase(String keyword);
}
