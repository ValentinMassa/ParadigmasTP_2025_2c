:- dynamic rol_requerido/2.
:- dynamic base_tiene_rol/2.
:- use_module(library(lists)).

min_trainings(Min) :-
    findall(Faltantes, (rol_requerido(Rol, _), faltantes_por_rol(Rol, Faltantes)), Diferencias),
    sum_list(Diferencias, Min).

faltantes_por_rol(Rol, Faltantes) :-
    rol_requerido(Rol, Total),
    cobertura_base(Rol, Cobertura),
    Necesita is Total - Cobertura,
    Faltantes is max(0, Necesita).

cobertura_base(Rol, Cobertura) :-
    findall(Artista, base_tiene_rol(Artista, Rol), Artistas),
    length(Artistas, Cobertura).

rol_faltante(Rol) :-
    faltantes_por_rol(Rol, Cantidad),
    Cantidad > 0.
