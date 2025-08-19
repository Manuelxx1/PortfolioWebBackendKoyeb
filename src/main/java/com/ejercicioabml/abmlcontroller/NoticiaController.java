@RestController
@RequestMapping("/api/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaRepository repository;

    @GetMapping("/buscar")
    public List<Noticia> buscar(@RequestParam String keyword) {
        return repository.findByInformacionContainingIgnoreCase(keyword);
    }
}
