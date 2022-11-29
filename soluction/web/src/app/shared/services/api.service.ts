import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {AngularFirestore} from '@angular/fire/compat/firestore';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from 'src/environments/environment';
import {CrearJuegoCommand} from '../commands/crearJuegoCommand';
import {UnirseAlJuegoCommand} from '../commands/unirseAlJuegoCommand';
import {IniciarJuegoCommand} from '../commands/iniciarJuegoCommand';
import {IniciarRondaCommand} from '../commands/iniciarRondaCommand';
import {PonerCartaEnTableroCommand} from '../commands/ponerCartaEnTableroCommand';
import {JuegoModel, Jugador} from '../model/juego';
import {TableroModel} from '../model/tablero';
import {User} from '../model/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  httpHeaders: HttpHeaders;

  constructor(private http: HttpClient, public afs: AngularFirestore, private auth: AuthService
  ) { 

    this.httpHeaders = new HttpHeaders({
      "Authorization": "Bearer " + auth.getToken()
    })
  }

  crearJuego(command: CrearJuegoCommand) {
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.post(environment.apiBaseCommand + '/juego/crear', command, httpOptions);
  }

  iniciarJuego(command: IniciarJuegoCommand){
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.post(environment.apiBaseCommand + '/juego/iniciar', command, httpOptions);
  }

  unirse(command: UnirseAlJuegoCommand){
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.post(environment.apiBaseCommand + '/juego/unirse', command, httpOptions);
  }

  iniciarRonda(command: IniciarRondaCommand){
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.post(environment.apiBaseCommand + '/juego/ronda/iniciar', command, httpOptions);
  }

  ponerCarta(command: PonerCartaEnTableroCommand){
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.post(environment.apiBaseCommand + '/juego/poner', command, httpOptions);
  }

  getJugadores(): Observable<Jugador[]> {
    return this.afs.collection<User>(`users`).snapshotChanges().pipe(map((actions) => {
      return actions.map(item => {
        const data = item.payload.doc.data();
        return {uid: data.uid, alias: data.displayName};
      });
    }));
  }

  getMisJuegos(uid: string): Observable<JuegoModel[]> {
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.get<JuegoModel[]>(environment.apiBaseQuery + '/juego/listar/' + uid, httpOptions);
   }

  getJuegos(): Observable<JuegoModel[]> {
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.get<JuegoModel[]>(environment.apiBaseQuery + '/juego/todos', httpOptions);
  }

  getMiMazo(uid: string, juegoId: string) {
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.get(environment.apiBaseQuery + '/juego/mazo/' + juegoId + '/' + uid, httpOptions);
  }

  getTablero(juegoId: string): Observable<TableroModel> {
    const httpOptions = {
      headers: this.httpHeaders
    };
    return this.http.get<TableroModel>(environment.apiBaseQuery + '/juego/tablero/' + juegoId, httpOptions);
  }
}
