import { BaseEntity } from './../../shared';

export class Videos implements BaseEntity {
    constructor(
        public id?: number,
        public nomePasta?: string,
        public titulo?: string,
        public nomeArquivo?: string,
        public extensaoArquivo?: string,
        public dataEnviado?: any,
        public visualizacoes?: number,
    ) {
    }
}
