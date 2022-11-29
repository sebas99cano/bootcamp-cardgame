import { Component, OnDestroy, OnInit } from '@angular/core';
import { ApiService } from 'src/app/shared/services/api.service';
import { AuthService } from 'src/app/shared/services/auth.service';
import { v1 as uuidv1 } from 'uuid';
import { WebsocketService } from 'src/app/shared/services/websocket.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-new-game',
  templateUrl: './new-game.component.html',
  styleUrls: ['./new-game.component.scss']
})
export class NewGameComponent implements OnInit, OnDestroy {
  juegoId: string;
  montoRequerido = new FormControl(0);

  constructor(private api: ApiService, private auth: AuthService, private socket:WebsocketService, private router:Router) {
     this.juegoId = uuidv1();
   }
 

  ngOnInit(): void {
    this.socket.open(this.juegoId);
    console.log(this.juegoId);
    this.socket.listener((event) => {
      if(event.type == "cardgame.jugadoragregado"){
        this.router.navigate(['list'])
      }
    });
  }

  ngOnDestroy(): void {
    this.socket.close();
  }

 

  onSubmit(){
    const monto = this.montoRequerido.getRawValue() || 0;
    this.api.crearJuego({
      jugadorPrincipalId: this.auth.user.uid,
      juegoId: this.juegoId,
      alias:  this.auth.user.displayName,
      montoRequerido: monto
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
    });
  }
}
