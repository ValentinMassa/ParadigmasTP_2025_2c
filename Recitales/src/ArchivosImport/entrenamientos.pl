% ============================================================================
% SISTEMA DE CALCULO DE ENTRENAMIENTOS MINIMOS
% ============================================================================
% Predicados dinámicos que serán asertados desde Java
:- dynamic rol_requerido/2.
:- dynamic base_tiene_rol/2.
:- dynamic artista_contratado/1.
:- use_module(library(lists)).

% ============================================================================
% PREDICADOS PRINCIPALES
% ============================================================================

% rol_cubierto_por_base(Rol)
% Verifica si un rol está cubierto por al menos un artista de la base
rol_cubierto_por_base(Rol) :-
    rol_requerido(Rol, _),
    base_tiene_rol(_, Rol).

% rol_faltante(Rol)
% Identifica roles que no están completamente cubiertos por la base
rol_faltante(Rol) :-
    faltantes_por_rol(Rol, Cantidad),
    Cantidad > 0.

% min_trainings(Min)
% Calcula el número mínimo de entrenamientos necesarios
% Es igual a la diferencia entre los roles requeridos y la cobertura de la base
min_trainings(Min) :-
    findall(Faltantes, (rol_requerido(Rol, _), faltantes_por_rol(Rol, Faltantes)), Diferencias),
    sum_list(Diferencias, Min).

% faltantes_por_rol(Rol, Cantidad)
% Calcula cuántos entrenamientos son necesarios para un rol específico
faltantes_por_rol(Rol, Faltantes) :-
    rol_requerido(Rol, Total),
    cobertura_base(Rol, Cobertura),
    Necesita is Total - Cobertura,
    Faltantes is max(0, Necesita).

% cobertura_base(Rol, Cobertura)
% Cuenta cuántos artistas de la base pueden tocar el rol
cobertura_base(Rol, Cobertura) :-
    findall(Artista, base_tiene_rol(Artista, Rol), Artistas),
    length(Artistas, Cobertura).

% ============================================================================
% PREDICADOS AUXILIARES
% ============================================================================

% listar_roles_requeridos
% Lista todos los roles requeridos para el recital
listar_roles_requeridos :-
    write('Roles requeridos:'), nl,
    forall(rol_requerido(Rol, Cantidad), (write('  - '), write(Rol), write(': '), write(Cantidad), nl)).

% listar_roles_cubiertos
% Lista todos los roles que están cubiertos por la base
listar_roles_cubiertos :-
    write('Roles cubiertos por la base:'), nl,
    forall(rol_cubierto_por_base(Rol), (write('  - '), write(Rol), nl)).

% listar_roles_faltantes
% Lista todos los roles que necesitan entrenamiento
listar_roles_faltantes :-
    write('Roles que requieren entrenamiento:'), nl,
    forall((rol_faltante(Rol), faltantes_por_rol(Rol, Cantidad), Cantidad > 0),
          (write('  - '), write(Rol), write(': '), write(Cantidad), nl)).

% diagnostico_completo
% Genera un diagnóstico completo del estado del recital
diagnostico_completo :-
    nl,
    write('========================================'), nl,
    write('  DIAGNOSTICO DE ENTRENAMIENTOS'), nl,
    write('========================================'), nl,
    nl,
    listar_roles_requeridos,
    nl,
    listar_roles_cubiertos,
    nl,
    listar_roles_faltantes,
    nl,
    min_trainings(Min),
    write('Total de entrenamientos necesarios: '), write(Min), nl,
    write('========================================'), nl.
