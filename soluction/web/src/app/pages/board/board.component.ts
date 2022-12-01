import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Carta } from "src/app/shared/model/mazo";
import { ApiService } from "src/app/shared/services/api.service";
import { AuthService } from "src/app/shared/services/auth.service";
import { WebsocketService } from "src/app/shared/services/websocket.service";
import Swal from "sweetalert2";

@Component({
  selector: "app-board",
  templateUrl: "./board.component.html",
  styleUrls: ["./board.component.scss"]
})
export class BoardComponent implements OnInit, OnDestroy {
  jugadoresIds: string[] = [];

  cartasDelJugador: Carta[] = [];
  cartasDelTablero: Carta[] = [];
  tiempo: number = 0;
  jugadoresRonda: number = 0;
  jugadoresTablero: number = 0;
  numeroRonda: number = 0;
  juegoId: string = "";
  uid: string = "";
  roundStarted: boolean = false;
  tableroHabilitado: boolean = false;

  constructor(
    public api: ApiService,
    public authService: AuthService,
    public socket: WebsocketService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.juegoId = params["id"];
      this.uid = this.authService.user.uid;

      this.api.getMiMazo(this.uid, this.juegoId).subscribe((element: any) => {
        if (element) {
          this.cartasDelJugador = element.cartas;
        }
      });

      this.api.getTablero(this.juegoId).subscribe(element => {
        if (element) {
          this.jugadoresIds = element.tablero.jugadores;

          Object.entries(element.tablero.cartas).forEach((a: any) => {
            const cards = Object.keys(a[1]);
            cards.forEach((d: any) => {
              this.cartasDelTablero.push(a[1][d]);
            });
          });

          this.tiempo = element.tiempo;
          this.jugadoresRonda = element.ronda.jugadores.length;
          this.jugadoresTablero = element.tablero.jugadores.length;
          this.numeroRonda = element.ronda.numero;
          this.roundStarted = element.ronda.estaIniciada;
          this.tableroHabilitado = element.tablero.habilitado;
        } else {
          Swal.fire({
            title: "El juego aún no tiene un tablero para el juego, por favor espere...",
            confirmButtonText: "OK",
            confirmButtonColor: "#9E1A00"
          });
        }
        this.initListener(this.juegoId);
      });
    });
  }

  initListener(juegoId: string) {
    this.socket.open(juegoId);

    this.socket.listener(event => {
      if (event.type == "cardgame.mazoasignadoajugador") {
        if(event.jugadorId.uuid === this.uid){
          this.reloadCurrentRoute();
        }
      }
      
      if (event.type === "cardgame.cartapuestaentablero") {
        this.cartasDelTablero.push({
          cartaId: event.carta.cartaId.uuid,
          poder: event.carta.poder,
          estaOculta: event.carta.estaHabilitada,
          estaHabilitada: event.carta.estaHabilitada,
          jugadorId: event.jugadorId.uuid
        });
      }

      if (event.type === "cardgame.cartaquitadadelmazo") {
        this.cartasDelJugador = this.cartasDelJugador.filter(
          item => item.cartaId !== event.carta.cartaId.uuid
        );
      }

      if (event.type === "cardgame.tiempocambiadodeltablero") {
        this.tiempo = event.tiempoLimite.value;
      }

      if (event.type === "cardgame.rondainiciada") {
        this.roundStarted = true;
        this.tableroHabilitado = true;
      }

      if (event.type === "cardgame.rondaterminada") {
        this.cartasDelTablero = [];
        this.roundStarted = false;
        this.tableroHabilitado = false;
      }

      if (event.type == "cardgame.rondacreada") {
        this.tiempo = event.tiempoLimite.value;
        this.jugadoresRonda = event.ronda.jugadores.length;
        this.numeroRonda = event.ronda.numero;
        this.tableroHabilitado = false;
        this.roundStarted = false;
      }

      if (event.type === "cardgame.juegofinalizado") {
        setTimeout(() => {
          Swal.fire({
            title: "Juego finalizado" + "\n" + "Ganador: " + event.alias.value,
            confirmButtonText: "Cerrar",
            confirmButtonColor: "#9E1A00"
          }).then(result => {
            this.router.navigate(["home"]);
          });
        }, 1700);
      }

      if (event.type === "cardgame.cartasasignadasajugador") {
        if (event.ganadorId.uuid === this.uid) {
          event.cartasApuesta.forEach((carta: any) => {
            this.cartasDelJugador.push({
              cartaId: carta.cartaId.uuid,
              poder: carta.poder,
              estaOculta: carta.estaOculta,
              estaHabilitada: carta.estaHabilitada,
              jugadorId: event.ganadorId.uuid
            });
          });
          Swal.fire({
            icon: "success",
            title: "Ganaste la ronda!",
            showConfirmButton: false,
            timer: 1500
          });
        } else {
          Swal.fire({
            icon: "error",
            title: "Perdiste la ronda :(",
            showConfirmButton: false,
            timer: 1500
          });
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.socket.close();
  }

  poner(cartaId: string) {
    this.api
      .ponerCarta({
        cartaId: cartaId,
        juegoId: this.juegoId,
        jugadorId: this.uid
      })
      .subscribe({
        error: error => {
          return Swal.fire({
            backdrop: "url(https://wallpaper.dog/large/20461297.jpg)",
            background: "#7C7C7C",
            grow: "fullscreen",
            icon: "warning",
            title: error.error.message,
            color: "white",
            showConfirmButton: false,
            timer: 2500
          });
        }
      });;
  }

  iniciarRonda() {
    this.api
      .iniciarRonda({
        juegoId: this.juegoId
      })
      .subscribe({
        error: error => {
          return Swal.fire({
            backdrop: "url(https://wallpaper.dog/large/20461297.jpg)",
            background: "#7C7C7C",
            grow: "fullscreen",
            icon: "warning",
            title: error.error.message,
            color: "white",
            showConfirmButton: false,
            timer: 2500
          });
        }
      });;
  }

  reloadCurrentRoute() {
    let currentUrl = this.router.url;
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
        this.router.navigate([currentUrl]);
    });
  }
}
