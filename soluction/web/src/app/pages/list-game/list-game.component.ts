import { Component, OnDestroy, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { ApiService } from "src/app/shared/services/api.service";
import { AuthService } from "src/app/shared/services/auth.service";
import { WebsocketService } from "src/app/shared/services/websocket.service";
import Swal from "sweetalert2";

@Component({
  selector: "app-list-game",
  templateUrl: "./list-game.component.html",
  styleUrls: ["./list-game.component.scss"]
})
export class ListGameComponent {
  juegos?: any;
  uid: string = "";
  alias: string = "";

  constructor(
    private api: ApiService,
    private router: Router,
    private auth: AuthService,
    private socket: WebsocketService
  ) {
    this.uid = JSON.parse(localStorage.getItem("user")!).uid;
    this.alias = JSON.parse(localStorage.getItem("user")!).displayName;

    this.api.getJuegos().subscribe(juegos => {
      this.juegos = juegos;
    });
  }

  joinGame(id: string) {
    this.socket.open(id);
    this.socket.listener(event => {
      if (event.type == "cardgame.jugadoragregado") {
        this.router.navigate(["board", id]);
        this.socket.close();
      }
    });
    this.api
      .unirse({
        juegoId: id,
        jugadorId: this.uid,
        alias: this.alias
      }).subscribe({
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

  showName(data: any) {
    const alias = data.alias;
    return alias.length > 10 ? `${alias.substring(0, 10)}...` : alias;
  }

  startGame(id: string) {
    this.socket.open(id);
    this.socket.listener(event => {
      if (event.type == "cardgame.rondacreada") {
        this.router.navigate(["board", id]);
        this.socket.close();
      }
    });
    this.api.iniciarJuego({ juegoId: id }).subscribe({
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
    });
  }
}
